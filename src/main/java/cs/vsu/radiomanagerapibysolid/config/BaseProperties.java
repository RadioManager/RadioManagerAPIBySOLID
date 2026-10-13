package cs.vsu.radiomanagerapibysolid.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.LocalTime;
import java.util.List;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "base")
public class BaseProperties {

    @Data
    public static class TimeWindow {
        @NotNull
        private LocalTime start;

        @NotNull
        private LocalTime end;
    }

    @NotNull
    @Positive
    private Double filePrice;

    @NotNull
    @Positive
    private Double priorityMultiplier;

    @NotEmpty
    @Valid
    private List<TimeWindow> priorityHigh;

    @Positive
    private long minSlotDuration;

}
