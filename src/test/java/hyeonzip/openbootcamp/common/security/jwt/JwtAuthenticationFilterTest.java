package hyeonzip.openbootcamp.common.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.user.domain.Role;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class JwtAuthenticationFilterTest {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtProvider jwtProvider;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ── 인증 설정 ──────────────────────────────────────────────────

    @Test
    @DisplayName("유효한 accessToken 쿠키가 있으면 SecurityContext에 인증 정보를 설정한다")
    void doFilter_validToken_setsAuthentication() throws Exception {
        String token = jwtProvider.generateAccessToken(1L, Role.STUDENT.name());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", token));

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(),
            new MockFilterChain());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(1L);
        assertThat(auth.getAuthorities())
            .extracting("authority")
            .containsExactly("ROLE_STUDENT");
    }

    @Test
    @DisplayName("토큰에서 userId가 정확히 추출된다")
    void doFilter_validToken_extractsCorrectUserId() throws Exception {
        Long expectedUserId = 42L;
        String token = jwtProvider.generateAccessToken(expectedUserId, Role.STUDENT.name());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", token));

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(),
            new MockFilterChain());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getPrincipal()).isEqualTo(expectedUserId);
    }

    @Test
    @DisplayName("ADMIN role 토큰이면 ROLE_ADMIN 권한으로 설정된다")
    void doFilter_adminToken_setsAdminAuthority() throws Exception {
        String token = jwtProvider.generateAccessToken(99L, Role.ADMIN.name());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", token));

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(),
            new MockFilterChain());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getAuthorities())
            .extracting("authority")
            .containsExactly("ROLE_ADMIN");
    }

    // ── 인증 미설정 ────────────────────────────────────────────────

    @Test
    @DisplayName("쿠키가 없으면 SecurityContext에 인증 정보를 설정하지 않는다")
    void doFilter_noCookie_doesNotSetAuthentication() throws Exception {
        jwtAuthenticationFilter.doFilter(new MockHttpServletRequest(),
            new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("유효하지 않은 토큰이면 SecurityContext에 인증 정보를 설정하지 않는다")
    void doFilter_invalidToken_doesNotSetAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", "invalid-token-value"));

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(),
            new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("accessToken이 아닌 다른 이름의 쿠키는 무시한다")
    void doFilter_wrongCookieName_doesNotSetAuthentication() throws Exception {
        String token = jwtProvider.generateAccessToken(1L, Role.STUDENT.name());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("otherCookie", token));

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(),
            new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // ── 필터 체인 ──────────────────────────────────────────────────

    @Test
    @DisplayName("인증 성공 여부와 관계없이 항상 다음 필터로 요청을 전달한다")
    void doFilter_alwaysContinuesFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(), filterChain);

        assertThat(filterChain.getRequest()).isNotNull();
    }
}
