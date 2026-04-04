package cs.vsu.radiomanagerapibysolid.dto;

import cs.vsu.radiomanagerapibysolid.model.enumerate.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AudioRecordingDto {

    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Double duration;

    @NotNull
    private Double cost;

    @NotNull
    private ApprovalStatus approvalStatus;

    @NotNull
    private String filePath;
}
