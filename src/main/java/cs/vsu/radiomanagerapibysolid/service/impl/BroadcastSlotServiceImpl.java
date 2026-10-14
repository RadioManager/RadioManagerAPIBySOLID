package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.config.BaseProperties;
import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;
import cs.vsu.radiomanagerapibysolid.exception.ConflictException;
import cs.vsu.radiomanagerapibysolid.exception.NotFoundException;
import cs.vsu.radiomanagerapibysolid.mapper.BroadcastSlotMapper;
import cs.vsu.radiomanagerapibysolid.model.BroadcastSlot;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Status;
import cs.vsu.radiomanagerapibysolid.repository.BroadcastSlotRep;
import cs.vsu.radiomanagerapibysolid.service.inter.AudioRecordingService;
import cs.vsu.radiomanagerapibysolid.service.inter.BroadcastSlotService;
import cs.vsu.radiomanagerapibysolid.service.inter.PricingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class BroadcastSlotServiceImpl implements BroadcastSlotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastSlotServiceImpl.class);

    private final BroadcastSlotRep broadcastSlotRep;

    private final BroadcastSlotMapper mapper;

    private final BaseProperties baseProperties;

    private final AudioRecordingService audioRecordingService;

    private final PricingService pricingService;

    @Override
    public List<BroadcastSlotDto> getAllBroadcastSlots() {
        LOGGER.debug("Fetching all broadcast slots");
        return mapper.toDtoList(broadcastSlotRep.findAll());
    }

    @Override
    public BroadcastSlotDto getBroadcastSlotById(Long id) {
        LOGGER.debug("Fetching broadcast slot by id: {}", id);
        return broadcastSlotRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public BroadcastSlotDto getBroadcastSlotByStartTimeAndEndTimeAndRadioStation(LocalDateTime startTime,
                                                                                 LocalDateTime endTime,
                                                                                 Long radioStationId) {
        LOGGER.debug(
                "Fetching broadcast slot by startTime and endTime and RadioStation: {}",
                startTime.toString() + " --- " + endTime.toString() + "by radio station with id: " + radioStationId
        );
        return broadcastSlotRep.findByStartTimeAndEndTimeAndRadioStationId(startTime, endTime, radioStationId)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public List<BroadcastSlotDto> getBroadcastSlotsByStatus(Status status) {
        LOGGER.debug("Fetching broadcast slot by status: {}", status);
        return mapper.toDtoList(broadcastSlotRep.findByStatus(status));
    }

    @Override
    public BroadcastSlotDto getBroadcastSlotByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        LOGGER.debug(
                "Fetching broadcast slot by startTime and endTime: {}",
                startTime.toString() + " --- " + endTime.toString()
        );
        return broadcastSlotRep.findByStartTimeAndEndTime(startTime, endTime)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public  List<BroadcastSlotDto> getBroadcastSlotByRadioStationId(Long radioStationId) {
        LOGGER.debug("Fetching broadcast slot by radio station with id: {}", radioStationId);
        return mapper.toDtoList(broadcastSlotRep.findByRadioStationId(radioStationId));
    }

    @Override
    public List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdWithStatus(Long radioStationId, Status status) {
        LOGGER.debug("Fetching broadcast slot by radio station with id: {}, with status: {}", radioStationId, status);
        return mapper.toDtoList(broadcastSlotRep.findByRadioStationIdAndStatus(radioStationId, status));
    }

    @Override
    public List<BroadcastSlotDto> getEmptyBroadcastSlotsByPriorityWithRadioStation(
            Long radioStationId, boolean highPriority) {
        LOGGER.debug("Fetching broadcast slot by radio station with id: {} and high priority is {}",
                radioStationId, highPriority);
        List<BroadcastSlotDto> slots = getBroadcastSlotsByRadioStationIdWithStatus(radioStationId, Status.AVAILABLE);
        return slots.stream()
                .filter(slot -> pricingService.isHighPriority(slot.getStartTime()) == highPriority)
                .toList();
    }

    @Override
    public BroadcastSlotDto createBroadcastSlot(BroadcastSlotDto broadcastSlotDto) {
        LOGGER.debug("Creating broadcast slot: {}", broadcastSlotDto);
        BroadcastSlot broadcastSlot = mapper.toEntity(broadcastSlotDto);
        broadcastSlot.setId(null);
        broadcastSlot = broadcastSlotRep.save(broadcastSlot);
        return mapper.toDto(broadcastSlot);
    }

    @Override
    public List<BroadcastSlotDto> createBroadcastSlots(List<BroadcastSlotDto> broadcastSlotsDto) {
        LOGGER.debug("Creating broadcast slots: {}", broadcastSlotsDto);
        List<BroadcastSlot> entities = mapper.toEntityList(broadcastSlotsDto);
        entities.forEach(slot -> slot.setId(null));

        List<BroadcastSlot> savedEntities = broadcastSlotRep.saveAll(entities);

        List<BroadcastSlotDto> result = mapper.toDtoList(savedEntities);
        LOGGER.info("Successfully created {} broadcast slots", result.size());
        return result;
    }

    @Override
    public BroadcastSlotDto updateBroadcastSlot(BroadcastSlotDto broadcastSlotDto) {
        LOGGER.debug("Updating broadcast slot: {}", broadcastSlotDto);
        Optional<BroadcastSlot> broadcastSlot = broadcastSlotRep.findById(broadcastSlotDto.getId());
        if (broadcastSlot.isPresent()) {
            BroadcastSlot broadcastSlotEntity = broadcastSlot.get();
            mapper.updateEntityFromDto(broadcastSlotDto, broadcastSlotEntity);
            BroadcastSlot updatedBroadcastSlot = broadcastSlotRep.save(broadcastSlotEntity);
            LOGGER.info("Updated broadcast slot: {}", updatedBroadcastSlot);
            return mapper.toDto(updatedBroadcastSlot);
        }
        LOGGER.warn("No broadcast slot found for update with id: {}", broadcastSlotDto.getId());
        return null;
    }

    @Override
    public boolean deleteBroadcastSlot(Long id) {
        LOGGER.debug("Deleting broadcast slot: {}", id);
        Optional<BroadcastSlot> broadcastSlot = broadcastSlotRep.findById(id);
        if (broadcastSlot.isPresent()) {
            BroadcastSlot broadcastSlotEntity = broadcastSlot.get();
            broadcastSlotRep.delete(broadcastSlotEntity);
            LOGGER.info("Deleted broadcast slot: {}", broadcastSlot);
            return true;
        }
        LOGGER.warn("No broadcast slot found for delete with id: {}", id);
        return false;
    }

    // TODO(диплом): удаляет и OCCUPIED-слоты вместе с оплаченными размещениями (каскад) — та же проблема,
    //   что в replaceFutureSlots. Используется там и в админском DELETE /broadcast-slot/station/{id}/after.
    @Override
    public boolean deleteBroadcastSlotsByRadioStationAfterStartTime(
            Long radioStationId, LocalDateTime startTime) {
        LOGGER.debug("Deleting broadcast slots of radio station {} after start time: {}",
                radioStationId, startTime);
        if (broadcastSlotRep.deleteAllByRadioStationIdAndStartTimeAfter(radioStationId, startTime) > 0) {
            LOGGER.info("Deleted broadcast slots of radio station {} after start time: {}",
                    radioStationId, startTime);
            return true;
        }
        LOGGER.warn("No broadcast slots found for delete");
        return false;
    }

    @Override
    public BroadcastSlotDto updateBroadcastSlotStatus(Long id, Status status) {
        LOGGER.debug("Updating broadcast slot status: {}", id);
        Optional<BroadcastSlot> broadcastSlot = broadcastSlotRep.findById(id);
        if (broadcastSlot.isPresent()) {
            BroadcastSlot broadcastSlotEntity = broadcastSlot.get();
            broadcastSlotEntity.setStatus(status);
            BroadcastSlot updatedBroadcastSlot = broadcastSlotRep.save(broadcastSlotEntity);
            LOGGER.info("Updated broadcast slot status: {}", updatedBroadcastSlot);
            return mapper.toDto(updatedBroadcastSlot);
        }
        LOGGER.warn("No broadcast slot found for update status with id: {}", id);
        return null;
    }

    @Override
    public BroadcastSlotDto updateBroadcastSlotEndTime(Long id, LocalDateTime endTime) {
        LOGGER.debug("Updating broadcast slot end time: {}", id);
        Optional<BroadcastSlot> broadcastSlot = broadcastSlotRep.findById(id);
        if (broadcastSlot.isPresent()) {
            BroadcastSlot broadcastSlotEntity = broadcastSlot.get();
            broadcastSlotEntity.setEndTime(endTime);
            BroadcastSlot updatedBroadcastSlot = broadcastSlotRep.save(broadcastSlotEntity);
            LOGGER.info("Updated broadcast slot endTime: {}", updatedBroadcastSlot);
            return mapper.toDto(updatedBroadcastSlot);
        }
        LOGGER.warn("No broadcast slot found for update end time with id: {}", id);
        return null;
    }

    @Override
    public BroadcastSlotDto splitBroadcastSlot(Long id, LocalDateTime newEndTime) {
        LOGGER.debug("Splitting broadcast slot with id: {}", id);
        BroadcastSlot broadcastSlot = broadcastSlotRep.findById(id)
                .orElseThrow(() -> new NotFoundException("Broadcast slot not found: " + id));

        if (newEndTime.isAfter(broadcastSlot.getEndTime())) {
            throw new ConflictException("New end time " + newEndTime
                    + " exceeds end time of broadcast slot " + id);
        }

        LocalDateTime originalEndTime = broadcastSlot.getEndTime();

        broadcastSlot.setEndTime(newEndTime);
        broadcastSlot.setStatus(Status.OCCUPIED);
        BroadcastSlot updatedBroadcastSlot = broadcastSlotRep.save(broadcastSlot);
        LOGGER.info("Updated original broadcast slot endTime and status: {}", updatedBroadcastSlot);

        if (Duration.between(newEndTime, originalEndTime)
                .compareTo(Duration.ofSeconds(baseProperties.getMinSlotDuration())) >= 0) {
            BroadcastSlot newBroadcastSlot = new BroadcastSlot();
            newBroadcastSlot.setStartTime(newEndTime);
            newBroadcastSlot.setEndTime(originalEndTime);
            newBroadcastSlot.setStatus(Status.AVAILABLE);
            newBroadcastSlot.setRadioStation(updatedBroadcastSlot.getRadioStation());

            BroadcastSlot savedNewBroadcastSlot = broadcastSlotRep.save(newBroadcastSlot);
            LOGGER.info("Created new broadcast slot with id: {}", savedNewBroadcastSlot.getId());
        }

        return mapper.toDto(updatedBroadcastSlot);
    }

    @Override
    public BroadcastSlotDto splitBroadcastSlotByAudio(Long slotId, Long audioId) {
        LOGGER.debug("Splitting broadcast slot {} by duration of audio recording {}", slotId, audioId);
        BroadcastSlot slot = broadcastSlotRep.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Broadcast slot not found: " + slotId));
        AudioRecordingDto audio = audioRecordingService.getRecordingById(audioId);
        if (audio == null) {
            throw new NotFoundException("Audio recording not found: " + audioId);
        }

        LocalDateTime newEndTime = slot.getStartTime()
                .plus(Duration.ofSeconds(Math.round(audio.getDuration())));
        return splitBroadcastSlot(slotId, newEndTime);
    }

    // TODO(диплом): удаляются и OCCUPIED-слоты, а с ними каскадом (ON DELETE CASCADE) оплаченные размещения —
    //   деньги рекламодателю не возвращаются. Варианты на диплом:
    //     - оплаченное важнее нового расписания: удалять только AVAILABLE, новые интервалы, пересекающиеся
    //       с занятыми слотами, обрезать (вычесть занятое время, если остаток >= min-slot-duration),
    //       а в ответе возвращать список того, что не применилось;
    //     - или отменять пересекающиеся размещения с возвратом денег и транзакцией возврата — для этого
    //       сначала хранить цену в placement (миграция: колонка price), сейчас она только пересчитывается.
    @Override
    public List<BroadcastSlotDto> replaceFutureSlots(Long radioStationId,
                                                     List<Pair<LocalDateTime, LocalDateTime>> timePairs) {
        LOGGER.debug("Replacing future broadcast slots of radio station {}", radioStationId);
        LocalDateTime now = LocalDateTime.now();

        List<Pair<LocalDateTime, LocalDateTime>> futureIntervals = timePairs.stream()
                .filter(pair -> !pair.getFirst().isBefore(now))
                .sorted(Comparator
                        .comparing(Pair<LocalDateTime, LocalDateTime>::getFirst)
                        .thenComparing(Pair::getSecond))
                .toList();

        deleteBroadcastSlotsByRadioStationAfterStartTime(radioStationId, now);
        broadcastSlotRep.flush();

        return createBroadcastSlots(createBroadcastSlotsDtoFromTimeAndStation(radioStationId, futureIntervals));
    }

    @Override
    public List<BroadcastSlotDto> getBroadcastSlotsByMonth(int year, int month) {
        LOGGER.debug("Fetching broadcast slots for year: {} and month: {}", year, month);
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        List<BroadcastSlot> slots = broadcastSlotRep.findByStartTimeBetween(start, end);
        return slots.stream()
                .sorted(Comparator.comparing(BroadcastSlot::getStartTime))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdAfterStartTime(Long id, LocalDateTime startTime) {
        LOGGER.debug("Fetching broadcast slots for radio station: {} after {}", id, startTime.toString());
        List<BroadcastSlot> slots = broadcastSlotRep.findByRadioStationIdAndStartTimeAfter(id, startTime);
        return slots.stream()
                .sorted(Comparator.comparing(BroadcastSlot::getStartTime))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdWithStatusAfterStartTime(
            Long id, Status status,LocalDateTime startTime) {
        LOGGER.debug("Fetching broadcast slots for radio station: {} with status: {} after {}",
                id, status,startTime.toString());
        List<BroadcastSlot> slots = broadcastSlotRep.findByRadioStationIdAndStatusAndStartTimeAfter(
                id, status, startTime);
        return slots.stream()
                .sorted(Comparator.comparing(BroadcastSlot::getStartTime))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BroadcastSlotDto> createBroadcastSlotsDtoFromTimeAndStation(
            Long stationId, List<Pair<LocalDateTime, LocalDateTime>> timePair) {
        LOGGER.debug("Creating broadcast slot dto for station: {}", stationId);
        List<BroadcastSlotDto> dtos = new ArrayList<>();
        BroadcastSlotDto newDto;
        for (Pair<LocalDateTime, LocalDateTime> pair : timePair) {
            newDto = new BroadcastSlotDto();
            newDto.setStartTime(pair.getFirst());
            newDto.setEndTime(pair.getSecond());
            newDto.setStatus(Status.AVAILABLE);
            newDto.setRadioStationId(stationId);
            dtos.add(newDto);
        }
        LOGGER.debug("Created {} broadcast slot DTOs for station {}", dtos.size(), stationId);
        return dtos;
    }

}
