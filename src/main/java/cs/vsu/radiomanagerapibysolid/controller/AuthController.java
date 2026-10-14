package cs.vsu.radiomanagerapibysolid.controller;

import cs.vsu.radiomanagerapibysolid.dto.UserDto;
import cs.vsu.radiomanagerapibysolid.dto.auth.AuthUserDto;
import cs.vsu.radiomanagerapibysolid.dto.auth.CodeDto;
import cs.vsu.radiomanagerapibysolid.dto.auth.PasswordChangeDto;
import cs.vsu.radiomanagerapibysolid.dto.auth.PasswordResetRequestDto;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Role;
import cs.vsu.radiomanagerapibysolid.security.JwtCookieProvider;
import cs.vsu.radiomanagerapibysolid.security.JwtProvider;
import cs.vsu.radiomanagerapibysolid.service.inter.AccessService;
import cs.vsu.radiomanagerapibysolid.service.inter.AuthService;
import cs.vsu.radiomanagerapibysolid.service.inter.ResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Login, registration, logout and password reset")
@AllArgsConstructor
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    private final AccessService accessService;

    private final ResetService resetService;

    private final JwtProvider jwtProvider;

    private final JwtCookieProvider jwtCookieProvider;

    @PostMapping
    @Operation(summary = "Authenticate user", description = "Authenticates a user and sets the JWT token in an httpOnly cookie")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthUserDto authUserDto) {
        LOGGER.info("Attempting to authenticate user: {}", authUserDto.getLogin());
        var userDto = authService.authenticate(authUserDto);
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
        }

        String token = jwtProvider.generateToken(userDto);
        ResponseCookie cookie = jwtCookieProvider.createCookie(token);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(userDto);
    }

    @PostMapping("/send-password-reset-code")
    @Operation(
            summary = "Send password reset code",
            description = "Sends a password reset code to the specified email address for resetting the password."
    )
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody @Valid PasswordResetRequestDto passwordResetRequestDto) {
        LOGGER.info("Attempting to send password reset code to email: {}", passwordResetRequestDto.getEmail());
        resetService.sendPasswordReset(passwordResetRequestDto.getEmail());
        LOGGER.info("Password reset code sent successfully to email: {}", passwordResetRequestDto.getEmail());
        return ResponseEntity.ok("Password reset code sent successfully.");
    }

    @PostMapping("/code-verification")
    @Operation(
            summary = "Verify code",
            description = "Validates the verification code by extracting the user ID from the token. Returns a success message if valid, or an error otherwise."
    )
    public ResponseEntity<?> codeVerification(@RequestBody @Valid CodeDto codeDto) {
        if (!jwtProvider.validateAccessToken(codeDto.getCode())) {
            LOGGER.warn("Verification failed: invalid or expired code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
        }

        LOGGER.info("Verification successful for user ID: {}", jwtProvider.getUserIdFromToken(codeDto.getCode()));
        return ResponseEntity.ok("Verification code is valid.");
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset Password",
            description = "Resets the user's password using the provided verification token and new password."
    )
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto) {
        String token = passwordChangeDto.getVerificationCode();
        if (!jwtProvider.validateAccessToken(token)) {
            LOGGER.warn("Password reset failed: invalid or expired code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
        }

        Long userId = jwtProvider.getUserIdFromToken(token);
        LOGGER.info("Resetting password for user ID: {}", userId);

        if (!resetService.updatePasswordById(userId, passwordChangeDto.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Registers a new user. Registration with ADMIN role is forbidden."
    )
    public ResponseEntity<?> register(@RequestBody @Valid UserDto userDto) {
        LOGGER.info("Attempting to register user with login: {}", userDto.getLogin());

        if (userDto.getRole() == Role.ADMIN) {
            LOGGER.warn("Registration attempt with ADMIN role is forbidden for login: {}", userDto.getLogin());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Registration with admin role is not allowed.");
        }

        userDto.setBalance(0.0);

        if (!authService.registerUser(userDto)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        }

        LOGGER.info("User registered successfully: {}", userDto.getLogin());
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Clears JWT cookie and logs out the user"
    )
    public ResponseEntity<?> logout() {
        LOGGER.info("Logging out user: {}", accessService.getCurrentUserId());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookieProvider.createExpiredCookie().toString())
                .body("Logged out successfully.");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get current user info",
            description = "Retrieves the currently authenticated user's information based on the JWT token stored in the HTTP-only cookie."
    )
    public ResponseEntity<UserDto> getCurrentUser() {
        Long userId = accessService.getCurrentUserId();
        LOGGER.info("Fetching current user: {}", userId);

        UserDto userDto = authService.getCurrentUser(userId);
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userDto);
    }

}
