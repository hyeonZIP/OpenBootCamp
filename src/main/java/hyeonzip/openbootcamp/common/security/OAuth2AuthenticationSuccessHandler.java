package hyeonzip.openbootcamp.common.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * GitHub OAuth2 로그인 성공 시 User upsert + JWT 발급을 담당하는 핸들러.
 * TODO: AuthService + JwtUtil 구현 후 내부 로직 완성
 */
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        // TODO: GitHub 사용자 정보(OidcUser / OAuth2User) 추출 → User upsert → JWT 발급 → 응답
    }
}
