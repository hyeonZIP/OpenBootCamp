package hyeonzip.openbootcamp.common.security.cookie;

import hyeonzip.openbootcamp.common.security.jwt.TokenPair;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String REFRESH_TOKEN_PATH = "/api/v1/auth/refresh";

    @Value("${app.jwt.access-token-expiry}")
    private long accessTokenExpiry;

    @Value("${app.jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
            .filter(c -> ACCESS_TOKEN_COOKIE.equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst();
    }

    public void addTokenCookies(HttpServletResponse response, TokenPair tokenPair) {
        addCookie(response, ACCESS_TOKEN_COOKIE, tokenPair.accessToken(),
            "/", Duration.ofMillis(accessTokenExpiry));
        addCookie(response, REFRESH_TOKEN_COOKIE, tokenPair.refreshToken(),
            REFRESH_TOKEN_PATH, Duration.ofMillis(refreshTokenExpiry));
    }

    public void clearTokenCookies(HttpServletResponse response) {
        addCookie(response, ACCESS_TOKEN_COOKIE, "", "/", Duration.ZERO);
        addCookie(response, REFRESH_TOKEN_COOKIE, "", REFRESH_TOKEN_PATH, Duration.ZERO);
    }

    private void addCookie(HttpServletResponse response, String name, String value,
        String path, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .httpOnly(true)
            .path(path)
            .maxAge(maxAge)
            .sameSite("Lax")
            // .secure(true)  // 프로덕션 배포 시 활성화
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
