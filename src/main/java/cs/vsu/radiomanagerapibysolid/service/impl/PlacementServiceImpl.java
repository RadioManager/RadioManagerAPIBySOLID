package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.config.BaseProperties;
import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;
import cs.vsu.radiomanagerapibysolid.dto.PlacementDto;
import cs.vsu.radiomanagerapibysolid.exception.ConflictException;
import cs.vsu.radiomanagerapibysolid.exception.NotFoundException;
import cs.vsu.radiomanagerapibysolid.mapper.PlacementMapper;
import cs.vsu.radiomanagerapibysolid.model.Placement;
import cs.vsu.radiomanagerapibysolid.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Status;
import cs.vsu.radiomanagerapibysolid.repository.PlacementRep;
import cs.vsu.radiomanagerapibysolid.service.inter.AccessService;
import cs.vsu.radiomanagerapibysolid.service.inter.AudioRecordingService;
import cs.vsu.radiomanagerapibysolid.service.inter.BroadcastSlotService;
import cs.vsu.radiomanagerapibysolid.service.inter.PlacementService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class PlacementServiceImpl implements PlacementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlacementServiceImpl.class);

    private final BaseProperties baseProperties;

    private final PlacementRep placementRep;

    private final PlacementMapper mapper;

    private final AudioRecordingService audioRecordingService;

    private final BroadcastSlotService broadcastSlotService;

    // TODO(ревью): новая зависимость — нужна для проверки «запись принадлежит текущему пользователю» в createPlacement
    private final AccessService accessService;

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

    // TODO(ревью): раньше эта логика жила в старом PlacementController.createPlacement, а сервис только
    //   сохранял запись. Перенесено из контроллера:
    //     - проверка «запись длиннее слота» → была 409 → теперь ConflictException (тоже 409). Сравнение
    //       оставлено как было (>= 0): запись ровно на длину слота тоже считается «не влезает»;
    //     - перевод слота в OCCUPIED после сохранения.
    //   Добавлено нового (в старом коде этого не было):
    //     - слот или запись не найдены → NotFoundException (404), раньше было NPE → 500;
    //     - слот уже не AVAILABLE → ConflictException (в таблице placement нет UNIQUE на slot_id, поэтому
    //       раньше один слот можно было продать дважды);
    //     - запись должна быть APPROVED и принадлежать текущему пользователю (или он ADMIN) → иначе
    //       ConflictException / AccessDeniedException (403). Фронт и так предлагает только свои одобренные
    //       записи (loadAllowedRecordings), но без проверки на бэке это обходилось подменой id в запросе.
    @Override
    public PlacementDto createPlacement(PlacementDto placementDto) {
        LOGGER.debug("Creating placement {}", placementDto);

        BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotById(placementDto.getBroadcastSlotId());
        if (slot == null) {
            throw new NotFoundException("Broadcast slot not found: " + placementDto.getBroadcastSlotId());
        }
        AudioRecordingDto audio = audioRecordingService.getRecordingById(placementDto.getAudioRecordingId());
        if (audio == null) {
            throw new NotFoundException("Audio recording not found: " + placementDto.getAudioRecordingId());
        }

        if (!accessService.isOwnerOrAdmin(audio.getUserId())) {
            throw new AccessDeniedException("Audio recording " + audio.getId() + " belongs to another user");
        }
        if (audio.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new ConflictException("Audio recording " + audio.getId() + " is not approved");
        }
        if (slot.getStatus() != Status.AVAILABLE) {
            throw new ConflictException("Broadcast slot " + slot.getId() + " is already occupied");
        }

        Duration audioDuration = Duration.ofSeconds(Math.round(audio.getDuration()));
        Duration slotDuration = Duration.between(slot.getStartTime(), slot.getEndTime());
        if (audioDuration.compareTo(slotDuration) >= 0) {
            throw new ConflictException("Audio recording " + audio.getId()
                    + " is longer than broadcast slot " + slot.getId());
        }

        Placement placement = mapper.toEntity(placementDto);
        placement.setId(null);
        placement = placementRep.save(placement);
        broadcastSlotService.updateBroadcastSlotStatus(slot.getId(), Status.OCCUPIED);
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
        BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotById(placementDto.getBroadcastSlotId());
        if (slot == null) {
            throw new NotFoundException("Broadcast slot not found: " + placementDto.getBroadcastSlotId());
        }
        AudioRecordingDto audio = audioRecordingService.getRecordingById(placementDto.getAudioRecordingId());
        if (audio == null) {
            throw new NotFoundException("Audio recording not found: " + placementDto.getAudioRecordingId());
        }

        Double baseCost = audio.getCost();

        LocalTime slotStart = slot.getStartTime().toLocalTime();

        boolean highPriority = baseProperties.getPriorityHigh().stream()
                .anyMatch(p ->
                        !slotStart.isBefore(p.getStart()) &&
                                slotStart.isBefore(p.getEnd())
                );

        double finalPrice = highPriority
                ? baseCost * baseProperties.getPriorityMultiplier()
                : baseCost;

        LOGGER.info("Computed placement price: {} (highPriority={}): base={} * multiplier={}",
                finalPrice, highPriority, baseCost, baseProperties.getPriorityMultiplier()
        );
        return finalPrice;
    }

}
