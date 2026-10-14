package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;

import java.time.LocalDateTime;

public interface PricingService {

    double calculateRecordingCost(double duration);

    boolean isHighPriority(LocalDateTime time);

    double getPriorityMultiplier(boolean highPriority);

    double calculatePlacementPrice(BroadcastSlotDto slot, AudioRecordingDto audio);

}
