package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;

import java.time.LocalDateTime;

// TODO(ревью): все правила цены в одном месте. Раньше они были размазаны по трём сервисам:
//   цена записи — AudioRecordingService.getCostByDuration, множитель — BroadcastSlotService.getPriorityMultiplier,
//   проверка «прайм-тайм» — продублирована в BroadcastSlotServiceImpl и PlacementServiceImpl.
public interface PricingService {

    double calculateRecordingCost(double duration);

    boolean isHighPriority(LocalDateTime time);

    double getPriorityMultiplier(boolean highPriority);

    double calculatePlacementPrice(BroadcastSlotDto slot, AudioRecordingDto audio);

}
