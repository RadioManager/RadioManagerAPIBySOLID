package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.dto.UserDto;
import cs.vsu.radiomanagerapibysolid.security.JwtProvider;
import cs.vsu.radiomanagerapibysolid.service.inter.AuthService;
import cs.vsu.radiomanagerapibysolid.service.inter.ResetService;
import cs.vsu.radiomanagerapibysolid.service.inter.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ResetServiceImpl implements ResetService {

    private final Logger LOGGER = LoggerFactory.getLogger(ResetServiceImpl.class);

    private final JwtProvider jwtProvider;

    private final AuthService authService;

    private final UserService userService;

    private final JavaMailSender mailSender;

    public void sendPasswordReset(String email) {
        LOGGER.info("Starting password reset process for email: {}", email);

        if (authService.checkEmailExists(email)) {
            UserDto user = userService.getUserByLogin(email);
            Long userId = user.getId();
            String token = jwtProvider.generatePasswordResetToken(userId);

            LOGGER.info("Password reset token generated successfully for user ID: {}", userId);

            String subject = "Password Reset Request";
            String text = "Your password reset code is: " + token;

            sendEmail(email, subject, text);

            LOGGER.info("Password reset email sent successfully to: {}", email);
        } else {
            LOGGER.error("Password reset failed - email not found: {}", email);
            throw new RuntimeException("User with email " + email + " not found");
        }
    }

    private void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("${spring.mail.username}");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public boolean updatePasswordByLogin(@NotNull String login, @NotNull String password) {
        LOGGER.info("Updating password by login: {}", login);
        Optional<UserDto> userOptional = Optional.ofNullable(userService.getUserByLogin(login));
        if (userOptional.isPresent()) {
            boolean success = userService.updatePassword(userOptional.get().getId(), password);
            LOGGER.info("Password updated successfully");
            return success;
        }
        return false;
    }

    public boolean updatePasswordById(Long userId, String newPassword) {
        LOGGER.info("Updating password for user ID: {}", userId);
        Optional<UserDto> userOptional = Optional.ofNullable(userService.getUserById(userId));
        if (userOptional.isPresent()) {
            boolean success = userService.updatePassword(userOptional.get().getId(), newPassword);
            LOGGER.info("Password updated successfully for user ID: {}", userId);
            return success;
        }
        LOGGER.warn("User not found with ID: {}", userId);
        return false;
    }

}
