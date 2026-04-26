package hyeonzip.openbootcamp.common.security.oauth2.handler;

import hyeonzip.openbootcamp.common.security.cookie.CookieProvider;
import hyeonzip.openbootcamp.common.security.jwt.JwtProvider;
import hyeonzip.openbootcamp.common.security.jwt.TokenPair;
import hyeonzip.openbootcamp.common.security.oauth2.CustomOAuth2User;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.service.ports.inp.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        User user = authService.upsertFromOAuth2(customUser.getUserInfo());

        TokenPair tokenPair = jwtProvider.issue(user.getId(), user.getRole().name());
        authService.saveRefreshToken(user, tokenPair.refreshToken());
        cookieProvider.addTokenCookies(response, tokenPair);

        response.sendRedirect(frontendUrl + "/auth/callback");
    }
}
