package hyeonzip.openbootcamp.common.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import hyeonzip.openbootcamp.common.security.cookie.CookieProvider;
import hyeonzip.openbootcamp.user.domain.Role;
import io.jsonwebtoken.Claims;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CookieProvider cookieProvider;

    @Mock
    private Claims mockClaims;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String VALID_TOKEN = "valid-token";
    private static final Long USER_ID = 1L;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ── 인증 설정 ──────────────────────────────────────────────────

    @Test
    @DisplayName("유효한 accessToken 쿠키가 있으면 SecurityContext에 인증 정보를 설정한다")
    void doFilter_validToken_setsAuthentication() throws Exception {
        when(cookieProvider.extractAccessToken(request)).thenReturn(Optional.of(VALID_TOKEN));
        when(jwtProvider.parseClaimsSafely(VALID_TOKEN)).thenReturn(Optional.of(mockClaims));
        when(jwtProvider.isAccessToken(mockClaims)).thenReturn(true);
        when(jwtProvider.getUserId(mockClaims)).thenReturn(USER_ID);
        when(jwtProvider.getRole(mockClaims)).thenReturn(Role.STUDENT.name());

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(USER_ID);
        assertThat(auth.getAuthorities())
            .extracting("authority")
            .containsExactly(Role.STUDENT.getAuthority());
    }

    @Test
    @DisplayName("ADMIN role 토큰이면 ROLE_ADMIN 권한으로 설정된다")
    void doFilter_adminToken_setsAdminAuthority() throws Exception {
        when(cookieProvider.extractAccessToken(request)).thenReturn(Optional.of(VALID_TOKEN));
        when(jwtProvider.parseClaimsSafely(VALID_TOKEN)).thenReturn(Optional.of(mockClaims));
        when(jwtProvider.isAccessToken(mockClaims)).thenReturn(true);
        when(jwtProvider.getUserId(mockClaims)).thenReturn(USER_ID);
        when(jwtProvider.getRole(mockClaims)).thenReturn(Role.ADMIN.name());

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getAuthorities())
            .extracting("authority")
            .containsExactly(Role.ADMIN.getAuthority());
    }

    // ── 인증 미설정 ────────────────────────────────────────────────

    @Test
    @DisplayName("쿠키가 없으면 SecurityContext에 인증 정보를 설정하지 않는다")
    void doFilter_noCookie_doesNotSetAuthentication() throws Exception {
        when(cookieProvider.extractAccessToken(request)).thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("토큰이 유효하지 않으면 SecurityContext에 인증 정보를 설정하지 않는다")
    void doFilter_invalidToken_doesNotSetAuthentication() throws Exception {
        when(cookieProvider.extractAccessToken(request)).thenReturn(Optional.of("invalid-token"));
        when(jwtProvider.parseClaimsSafely("invalid-token")).thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("refreshToken이면 타입 체크에서 거부되어 SecurityContext에 인증 정보를 설정하지 않는다")
    void doFilter_refreshToken_doesNotSetAuthentication() throws Exception {
        when(cookieProvider.extractAccessToken(request)).thenReturn(Optional.of(VALID_TOKEN));
        when(jwtProvider.parseClaimsSafely(VALID_TOKEN)).thenReturn(Optional.of(mockClaims));
        when(jwtProvider.isAccessToken(mockClaims)).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("role claim이 없는 토큰이면 SecurityContext에 빈 권한 리스트가 설정된다")
    void doFilter_tokenWithNoRoleClaim_doesNotSetAuthentication() throws Exception {
        when(cookieProvider.extractAccessToken(request)).thenReturn(Optional.of(VALID_TOKEN));
        when(jwtProvider.parseClaimsSafely(VALID_TOKEN)).thenReturn(Optional.of(mockClaims));
        when(jwtProvider.isAccessToken(mockClaims)).thenReturn(true);
        when(jwtProvider.getRole(mockClaims)).thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(
            SecurityContextHolder.getContext().getAuthentication().getAuthorities()).isEmpty();
    }

    // ── 필터 체인 ──────────────────────────────────────────────────

    @Test
    @DisplayName("인증 성공 여부와 관계없이 항상 다음 필터로 요청을 전달한다")
    void doFilter_alwaysContinuesFilterChain() throws Exception {
        when(cookieProvider.extractAccessToken(request)).thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(filterChain.getRequest()).isNotNull();
    }
}
