package cs.vsu.radiomanagerapibysolid.service.inter;

import cs.vsu.radiomanagerapibysolid.dto.UserDto;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Role;
import lombok.NonNull;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    List<UserDto> getUsersByRole(Role role);

    UserDto getUserByLogin(String login);

    UserDto createUser(UserDto userDto);

    boolean deleteUser(Long id);

    UserDto updateUser(@NonNull UserDto userDTO);

    boolean updatePassword(Long userId, String newPassword);

    boolean updateBalance(Long userId, Double balance);

    Role getRoleById(Long id);

    UserDto getSystemEntity();

}
