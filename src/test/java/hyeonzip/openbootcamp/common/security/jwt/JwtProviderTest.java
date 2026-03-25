package hyeonzip.openbootcamp.common.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.user.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    private static final String SECRET = "test-secret-key-minimum-32-characters-long";
    private static final long ACCESS_TOKEN_EXPIRY = 900_000L;    // 15분
    private static final long REFRESH_TOKEN_EXPIRY = 604_800_000L; // 7일

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret", SECRET);
        ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiry", ACCESS_TOKEN_EXPIRY);
        ReflectionTestUtils.setField(jwtProvider, "refreshTokenExpiry", REFRESH_TOKEN_EXPIRY);
    }

    // ── accessToken 생성 ──────────────────────────────────────────

    @Test
    @DisplayName("accessToken에서 userId가 정확히 추출된다")
    void generateAccessToken_extractsCorrectUserId() {
        String token = jwtProvider.generateAccessToken(1L, Role.STUDENT.name());

        assertThat(jwtProvider.getUserId(token)).isEqualTo(1L);
    }

    @Test
    @DisplayName("accessToken에서 role이 정확히 추출된다")
    void generateAccessToken_extractsCorrectRole() {
        String token = jwtProvider.generateAccessToken(1L, Role.ADMIN.name());

        assertThat(jwtProvider.getRole(token)).isEqualTo(Role.ADMIN.name());
    }

    // ── refreshToken 생성 ─────────────────────────────────────────

    @Test
    @DisplayName("refreshToken에서 userId가 정확히 추출된다")
    void generateRefreshToken_extractsCorrectUserId() {
        String token = jwtProvider.generateRefreshToken(42L);

        assertThat(jwtProvider.getUserId(token)).isEqualTo(42L);
    }

    // ── isTokenValid ──────────────────────────────────────────────

    @Test
    @DisplayName("유효한 토큰은 true를 반환한다")
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtProvider.generateAccessToken(1L, Role.STUDENT.name());

        assertThat(jwtProvider.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰은 false를 반환한다")
    void isTokenValid_malformedToken_returnsFalse() {
        assertThat(jwtProvider.isTokenValid("not-a-jwt")).isFalse();
    }

    @Test
    @DisplayName("다른 시크릿으로 서명된 토큰은 false를 반환한다")
    void isTokenValid_differentSecretToken_returnsFalse() {
        JwtProvider otherProvider = new JwtProvider();
        ReflectionTestUtils.setField(otherProvider, "secret",
            "other-secret-key-minimum-32-characters-long");
        ReflectionTestUtils.setField(otherProvider, "accessTokenExpiry", ACCESS_TOKEN_EXPIRY);
        ReflectionTestUtils.setField(otherProvider, "refreshTokenExpiry", REFRESH_TOKEN_EXPIRY);

        String tokenFromOther = otherProvider.generateAccessToken(1L, Role.STUDENT.name());

        assertThat(jwtProvider.isTokenValid(tokenFromOther)).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 false를 반환한다")
    void isTokenValid_expiredToken_returnsFalse() {
        JwtProvider expiredProvider = new JwtProvider();
        ReflectionTestUtils.setField(expiredProvider, "secret", SECRET);
        ReflectionTestUtils.setField(expiredProvider, "accessTokenExpiry", -1L);
        ReflectionTestUtils.setField(expiredProvider, "refreshTokenExpiry", REFRESH_TOKEN_EXPIRY);

        String expiredToken = expiredProvider.generateAccessToken(1L, Role.STUDENT.name());

        assertThat(jwtProvider.isTokenValid(expiredToken)).isFalse();
    }
}
