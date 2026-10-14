package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;
import cs.vsu.radiomanagerapibysolid.dto.PlacementDto;
import cs.vsu.radiomanagerapibysolid.exception.NotFoundException;
import cs.vsu.radiomanagerapibysolid.mapper.PlacementMapper;
import cs.vsu.radiomanagerapibysolid.model.Placement;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Status;
import cs.vsu.radiomanagerapibysolid.repository.PlacementRep;
import cs.vsu.radiomanagerapibysolid.service.inter.AudioRecordingService;
import cs.vsu.radiomanagerapibysolid.service.inter.BillingService;
import cs.vsu.radiomanagerapibysolid.service.inter.BroadcastSlotService;
import cs.vsu.radiomanagerapibysolid.service.inter.PlacementService;
import cs.vsu.radiomanagerapibysolid.service.inter.PricingService;
import cs.vsu.radiomanagerapibysolid.validator.PlacementValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class PlacementServiceImpl implements PlacementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlacementServiceImpl.class);

    private static final String PLACEMENT_CHARGE_DESCRIPTION = "Размещение рекламы";

    private final PlacementRep placementRep;

    private final PlacementMapper mapper;

    private final AudioRecordingService audioRecordingService;

    private final BroadcastSlotService broadcastSlotService;

    private final PlacementValidator placementValidator;

    private final PricingService pricingService;

    private final BillingService billingService;

    @Override
    public List<PlacementDto> getAll() {
        LOGGER.debug("Fetching all placements");
        return mapper.toDtoList(placementRep.findAll());
    }

    @Override
    public PlacementDto getById(Long id) {
        LOGGER.debug("Fetching placement by id: {}", id);
        return placementRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public List<PlacementDto> getByPlacementDate(LocalDateTime date) {
        LOGGER.debug("Fetching placements by date {}", date);
        return mapper.toDtoList(placementRep.findByPlacementDate(date));
    }

    @Override
    public List<PlacementDto> getByAudioRecordingId(Long id) {
        LOGGER.debug("Fetching placements by audio recording id {}", id);
        return mapper.toDtoList(placementRep.findByAudioRecordingId(id));
    }

    @Override
    public PlacementDto getByBroadcastSlotId(Long id) {
        LOGGER.debug("Fetching placement by broadcast slot id {}", id);
        return mapper.toDto(placementRep.findByBroadcastSlotId(id).orElse(null));
    }

    // TODO(ревью): сценарий создания размещения. В старом проекте всё было в PlacementController, потом
    //   временно в этом методе; теперь каждая часть в своём классе (SRP):
    //     - права (своя запись или ADMIN) — @PreAuthorize в PlacementController;
    //     - бизнес-правила (запись одобрена, слот свободен, запись влезает) — PlacementValidator;
    //     - цена (прайм-тайм × множитель) — PricingService;
    //     - деньги (проверка баланса, списание, системная транзакция) — BillingService. Платит владелец записи.
    //   Метод под @Transactional: если charge бросит «не хватает денег», откатятся и размещение, и статус слота.
    @Override
    public PlacementDto createPlacement(PlacementDto placementDto) {
        LOGGER.debug("Creating placement {}", placementDto);
        BroadcastSlotDto slot = getSlotOrThrow(placementDto.getBroadcastSlotId());
        AudioRecordingDto audio = getAudioOrThrow(placementDto.getAudioRecordingId());

        placementValidator.validate(slot, audio);
        double price = pricingService.calculatePlacementPrice(slot, audio);

        Placement placement = mapper.toEntity(placementDto);
        placement.setId(null);
        placement = placementRep.save(placement);
        broadcastSlotService.updateBroadcastSlotStatus(slot.getId(), Status.OCCUPIED);

        billingService.charge(audio.getUserId(), price, PLACEMENT_CHARGE_DESCRIPTION);
        LOGGER.debug("Placement created: {}", placement);
        return mapper.toDto(placement);
    }

    @Override
    public PlacementDto updatePlacement(PlacementDto placementDto) {
        LOGGER.debug("Updating placement {}", placementDto);
        Optional<Placement> optional = placementRep.findById(placementDto.getId());
        if (optional.isPresent()) {
            Placement placement = optional.get();
            mapper.updateEntityFromDto(placementDto, placement);
            Placement updated = placementRep.save(placement);
            LOGGER.debug("Placement updated: {}", updated);
            return mapper.toDto(updated);
        }
        LOGGER.warn("Placement with id {} not found", placementDto.getId());
        return null;
    }

    @Override
    public boolean deletePlacement(Long id) {
        LOGGER.debug("Deleting placement {}", id);
        Optional<Placement> optional = placementRep.findById(id);
        if (optional.isPresent()) {
            Placement placement = optional.get();
            placementRep.delete(placement);
            LOGGER.debug("Placement deleted: {}", placement);
            return true;
        }
        LOGGER.warn("Placement with id for delete {} not found", id);
        return false;
    }

    @Override
    public Double getPlacementPrice(PlacementDto placementDto) {
        LOGGER.debug("Getting a price of placement {}", placementDto);
        BroadcastSlotDto slot = getSlotOrThrow(placementDto.getBroadcastSlotId());
        AudioRecordingDto audio = getAudioOrThrow(placementDto.getAudioRecordingId());
        return pricingService.calculatePlacementPrice(slot, audio);
    }

    private BroadcastSlotDto getSlotOrThrow(Long slotId) {
        BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotById(slotId);
        if (slot == null) {
            throw new NotFoundException("Broadcast slot not found: " + slotId);
        }
        return slot;
    }

    private AudioRecordingDto getAudioOrThrow(Long audioId) {
        AudioRecordingDto audio = audioRecordingService.getRecordingById(audioId);
        if (audio == null) {
            throw new NotFoundException("Audio recording not found: " + audioId);
        }
        return audio;
    }

}
