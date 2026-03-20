package hyeonzip.openbootcamp.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 토큰을 검증하고 SecurityContext에 인증 정보를 등록하는 필터.
 * TODO: JwtUtil 구현 후 내부 로직 완성
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        // TODO: Authorization 헤더에서 Bearer 토큰 추출 → 검증 → SecurityContext 등록
        filterChain.doFilter(request, response);
    }
}
