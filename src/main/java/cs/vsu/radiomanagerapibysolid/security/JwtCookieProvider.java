package cs.vsu.radiomanagerapibysolid.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtCookieProvider {

    public static final String JWT_COOKIE_NAME = "jwt";

    private final JwtProvider jwtProvider;

    public ResponseCookie createCookie(String token) {
        return buildCookie(token)
                .maxAge(jwtProvider.getAccessTokenTtl())
                .build();
    }

    public ResponseCookie createExpiredCookie() {
        return buildCookie("")
                .maxAge(0)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder buildCookie(String value) {
        return ResponseCookie.from(JWT_COOKIE_NAME, value)
                .path("/")
                .httpOnly(true)
                .secure(false);
    }

}
