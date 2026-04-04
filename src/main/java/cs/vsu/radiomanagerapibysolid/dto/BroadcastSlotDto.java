package cs.vsu.radiomanagerapibysolid.dto;

import cs.vsu.radiomanagerapibysolid.model.enumerate.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BroadcastSlotDto {

    @NotNull
    private Long id;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Status status;

    @NotNull
    private Long radioStationId;

}
