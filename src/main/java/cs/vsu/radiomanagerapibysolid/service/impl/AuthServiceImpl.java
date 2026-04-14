package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.dto.UserDto;
import cs.vsu.radiomanagerapibysolid.dto.auth.AuthUserDto;
import cs.vsu.radiomanagerapibysolid.service.inter.AuthService;
import cs.vsu.radiomanagerapibysolid.service.inter.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserDto authenticate(@NonNull AuthUserDto authUserDto) {
        LOGGER.info("Authenticating user with login: {}", authUserDto.getLogin());
        Optional<UserDto> userOptional = Optional.ofNullable(userService.getUserByLogin(authUserDto.getLogin()));
        if (userOptional.isPresent()) {
            UserDto user = userOptional.get();
            if (passwordEncoder.matches(authUserDto.getPassword(), user.getPassword())) {
                LOGGER.info("Authentication successful for user: {}", user.getLogin());
                return user;
            } else {
                LOGGER.warn("Authentication failed for user: {}", authUserDto.getLogin());
            }
        } else {
            LOGGER.warn("User not found with login: {}", authUserDto.getLogin());
        }
        return null;
    }

    public boolean checkEmailExists(@NotNull String email) {
        LOGGER.info("Checking if email exists: {}", email);
        Optional<UserDto> userOptional = Optional.ofNullable(userService.getUserByLogin(email));
        boolean exists = userOptional.isPresent();
        if (exists) {
            LOGGER.info("Email exists: {}", email);
        } else {
            LOGGER.warn("Email does not exist: {}", email);
        }
        return exists;
    }

    public boolean registerUser(@NotNull UserDto userDto) {
        LOGGER.info("Registering user with ID: {}", userDto.getId());
        Optional<UserDto> userOptional = Optional.ofNullable(userService.getUserById(userDto.getId()));
        if (userOptional.isEmpty()) {
            userService.createUser(userDto);
            LOGGER.info("User created successfully");
            return true;
        }
        LOGGER.warn("User already exists with ID: {}", userDto.getId());
        return false;
    }

    public UserDto getCurrentUser(Long id) {
        LOGGER.info("Getting current user with ID: {}", id);
        Optional<UserDto> userOptional = Optional.ofNullable(userService.getUserById(id));
        if (userOptional.isPresent()) {
            LOGGER.info("Successfully retrieved user with ID: {} ", id);
            return userOptional.get();
        }
        LOGGER.warn("User not found with ID: {}", id);
        return null;
    }

}
