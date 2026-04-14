package cs.vsu.radiomanagerapibysolid.dto;

import cs.vsu.radiomanagerapibysolid.model.enumerate.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {


    private Long id;

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotNull
    private Double balance;

    @NotNull
    private Role role;

}
