package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.config.BaseProperties;
import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;
import cs.vsu.radiomanagerapibysolid.service.inter.PricingService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@AllArgsConstructor
public class PricingServiceImpl implements PricingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PricingServiceImpl.class);

    private final BaseProperties baseProperties;

    @Override
    public double calculateRecordingCost(double duration) {
        return baseProperties.getFilePrice() * duration;
    }

    @Override
    public boolean isHighPriority(LocalDateTime time) {
        LocalTime localTime = time.toLocalTime();
        return baseProperties.getPriorityHigh().stream()
                .anyMatch(window -> !localTime.isBefore(window.getStart()) && localTime.isBefore(window.getEnd()));
    }

    @Override
    public double getPriorityMultiplier(boolean highPriority) {
        LOGGER.debug("Fetching priority multiplier for priority: {}", highPriority);
        return highPriority ? baseProperties.getPriorityMultiplier() : 1.0;
    }

    @Override
    public double calculatePlacementPrice(BroadcastSlotDto slot, AudioRecordingDto audio) {
        boolean highPriority = isHighPriority(slot.getStartTime());
        double multiplier = getPriorityMultiplier(highPriority);
        double price = audio.getCost() * multiplier;

        LOGGER.info("Computed placement price: {} (highPriority={}): base={} * multiplier={}",
                price, highPriority, audio.getCost(), multiplier);
        return price;
    }

}
