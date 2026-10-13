package cs.vsu.radiomanagerapibysolid.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CodeDto {

    @NotBlank
    private String code;

}
