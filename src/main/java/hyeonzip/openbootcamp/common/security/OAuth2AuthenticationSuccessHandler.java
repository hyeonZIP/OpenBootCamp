package hyeonzip.openbootcamp.common.security;

import hyeonzip.openbootcamp.common.security.oauth2.CustomOAuth2User;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.service.ports.inp.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.jwt.access-token-expiry}")
    private long accessTokenExpiry;

    @Value("${app.jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        User user = authService.upsertFromOAuth2(customUser.getUserInfo());

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        addCookie(response, "accessToken", accessToken, "/", Duration.ofMillis(accessTokenExpiry));
        addCookie(response, "refreshToken", refreshToken, "/api/v1/auth/refresh",
            Duration.ofMillis(refreshTokenExpiry));

        response.sendRedirect(frontendUrl + "/auth/callback");
    }

    private void addCookie(HttpServletResponse response, String name, String value, String path,
        Duration maxAge) {
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
