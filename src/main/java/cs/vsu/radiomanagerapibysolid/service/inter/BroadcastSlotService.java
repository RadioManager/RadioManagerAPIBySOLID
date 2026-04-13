package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Status;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.List;

public interface BroadcastSlotService {

    List<BroadcastSlotDto> getAllBroadcastSlots();

    BroadcastSlotDto getBroadcastSlotById(Long id);

    BroadcastSlotDto getBroadcastSlotByStartTimeAndEndTimeAndRadioStation(LocalDateTime startTime,
                                                                          LocalDateTime endTime,
                                                                          Long radioStationId);

    List<BroadcastSlotDto> getBroadcastSlotsByStatus(Status status);

    BroadcastSlotDto getBroadcastSlotByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);

    List<BroadcastSlotDto> getBroadcastSlotByRadioStationId(Long radioStationId);

    List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdWithStatus(Long radioStationId, Status status);

    List<BroadcastSlotDto> getEmptyBroadcastSlotsByPriorityWithRadioStation(Long radioStationId, boolean highPriority);

    BroadcastSlotDto createBroadcastSlot(BroadcastSlotDto broadcastSlotDto);

    List<BroadcastSlotDto> createBroadcastSlots(List<BroadcastSlotDto> broadcastSlotsDto);

    BroadcastSlotDto updateBroadcastSlot(BroadcastSlotDto broadcastSlotDto);

    boolean deleteBroadcastSlot(Long id);

    boolean deleteBroadcastSlotsByRadioStationAfterStartTime(Long radioStationId, LocalDateTime startTime);

    BroadcastSlotDto updateBroadcastSlotStatus(Long id, Status status);

    BroadcastSlotDto updateBroadcastSlotEndTime(Long id, LocalDateTime endTime);

    BroadcastSlotDto splitBroadcastSlot(Long id, LocalDateTime newEndTime);

    List<BroadcastSlotDto> getBroadcastSlotsByMonth(int year, int month);

    List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdAfterStartTime(Long id, LocalDateTime startTime);

    List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdWithStatusAfterStartTime(Long id,
                                                                                     Status status,
                                                                                     LocalDateTime startTime);

    List<BroadcastSlotDto> createBroadcastSlotsDtoFromTimeAndStation(Long stationId,
                                                                     List<Pair<LocalDateTime, LocalDateTime>> timePair);

    double getPriorityMultiplier(boolean priority);

}
