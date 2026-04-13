package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.UserDto;
import cs.vsu.radiomanagerapibysolid.dto.auth.AuthUserDto;

public interface AuthService {

    UserDto authenticate(AuthUserDto authUserDto);

    boolean checkEmailExists(String email);

    boolean registerUser(UserDto userDto);

    UserDto getCurrentUser(Long id);

}
