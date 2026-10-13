package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.config.SystemEntityProperties;
import cs.vsu.radiomanagerapibysolid.dto.UserDto;
import cs.vsu.radiomanagerapibysolid.mapper.UserMapper;
import cs.vsu.radiomanagerapibysolid.model.User;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Role;
import cs.vsu.radiomanagerapibysolid.repository.UserRep;
import cs.vsu.radiomanagerapibysolid.service.inter.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRep userRep;

    private final UserMapper mapper;

    private final BCryptPasswordEncoder passwordEncoder;

    private final SystemEntityProperties systemEntityProperties;

    @Override
    public List<UserDto> getAllUsers() {
        LOGGER.debug("Fetching all users");
        return mapper.toDtoList(userRep.findAll());
    }

    @Override
    public UserDto getUserById(Long id) {
        LOGGER.debug("Fetching user with password by id: {}", id);
        return userRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public List<UserDto> getUsersByRole(Role role) {
        LOGGER.debug("Fetching users by role: {}", role);
        return mapper.toDtoList(userRep.findByRole(role));
    }

    @Override
    public UserDto getUserByLogin(String login) {
        LOGGER.debug("Fetching user without password by login: {}", login);
        return userRep.findByLogin(login)
                .map(mapper::toDto)
                .orElse(null);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.toEntity(userDto);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user = userRep.save(user);
        LOGGER.info("User created: {}", userDto.getLogin());
        return mapper.toDto(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        Optional<User> user = userRep.findById(id);
        if (user.isPresent()) {
            userRep.deleteById(Math.toIntExact(id));
            LOGGER.info("User deleted with id: {}", id);
            return true;
        }
        return false;
    }

    @Override
    public UserDto updateUser(@NonNull UserDto userDTO) {
        Optional<User> existingUserOptional = userRep.findById(userDTO.getId());
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            mapper.updateEntityFromDto(userDTO, existingUser);
            User updatedUser = userRep.save(existingUser);
            LOGGER.info("User updated: {}", userDTO.getId());
            return mapper.toDto(updatedUser);
        }
        LOGGER.warn("User with id {} not found for update", userDTO.getId());
        return null;
    }

    @Override
    public boolean updatePassword(Long userId, String newPassword) {
        Optional<User> existingUserOptional = userRep.findById(userId);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userRep.save(existingUser);
            LOGGER.info("User password updated for user id: {}", userId);
            return true;
        }
        LOGGER.warn("User with id {} not found for password update", userId);
        return false;
    }

    @Override
    public boolean updateBalance(Long userId, Double balance) {
        Optional<User> existingUserOptional = userRep.findById(userId);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setBalance(balance);
            userRep.save(existingUser);
            LOGGER.info("User balance updated for user id: {}", userId);
            return true;
        }
        LOGGER.warn("User with id {} not found for balance update", userId);
        return false;
    }

    @Override
    public Role getRoleById(Long id) {
        LOGGER.debug("Getting role by id: {}", id);
        return getUserById(id).getRole();
    }

    @Override
    public UserDto getSystemEntity() {
        LOGGER.debug("Fetching system entity");
        String login = systemEntityProperties.getLogin();
        UserDto systemEntity = getUserByLogin(login);
        if (systemEntity != null) {
            LOGGER.debug("Fetched system entity");
            return systemEntity;
        }
        throw new IllegalStateException("System entity not found for login: " + login);
    }
}
