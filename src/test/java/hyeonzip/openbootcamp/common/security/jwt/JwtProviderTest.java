package hyeonzip.openbootcamp.common.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.user.domain.Role;
import io.jsonwebtoken.Claims;
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

    // ── issue + getUserId / getRole ────────────────────────────────

    @Test
    @DisplayName("accessToken Claims에서 userId가 정확히 추출된다")
    void issue_accessToken_extractsCorrectUserId() {
        TokenPair pair = jwtProvider.issue(1L, Role.STUDENT.name());

        Claims claims = jwtProvider.parseClaimsSafely(pair.accessToken()).orElseThrow();
        assertThat(jwtProvider.getUserId(claims)).isEqualTo(1L);
    }

    @Test
    @DisplayName("accessToken Claims에서 role이 정확히 추출된다")
    void issue_accessToken_extractsCorrectRole() {
        TokenPair pair = jwtProvider.issue(1L, Role.ADMIN.name());

        Claims claims = jwtProvider.parseClaimsSafely(pair.accessToken()).orElseThrow();
        assertThat(jwtProvider.getRole(claims)).isEqualTo(Role.ADMIN.name());
    }

    @Test
    @DisplayName("refreshToken Claims에서 userId가 정확히 추출된다")
    void issue_refreshToken_extractsCorrectUserId() {
        TokenPair pair = jwtProvider.issue(42L, Role.STUDENT.name());

        Claims claims = jwtProvider.parseClaimsSafely(pair.refreshToken()).orElseThrow();
        assertThat(jwtProvider.getUserId(claims)).isEqualTo(42L);
    }

    @Test
    @DisplayName("refreshToken은 role claim을 포함하지 않는다")
    void issue_refreshToken_hasNoRoleClaim() {
        TokenPair pair = jwtProvider.issue(1L, Role.STUDENT.name());

        Claims claims = jwtProvider.parseClaimsSafely(pair.refreshToken()).orElseThrow();
        assertThat(jwtProvider.getRole(claims)).isNull();
    }

    // ── parseClaimsSafely ──────────────────────────────────────────

    @Test
    @DisplayName("유효한 토큰은 Optional.present를 반환한다")
    void parseClaimsSafely_validToken_returnsPresent() {
        TokenPair pair = jwtProvider.issue(1L, Role.STUDENT.name());

        assertThat(jwtProvider.parseClaimsSafely(pair.accessToken())).isPresent();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰은 Optional.empty를 반환한다")
    void parseClaimsSafely_malformedToken_returnsEmpty() {
        assertThat(jwtProvider.parseClaimsSafely("not-a-jwt")).isEmpty();
    }

    @Test
    @DisplayName("다른 시크릿으로 서명된 토큰은 Optional.empty를 반환한다")
    void parseClaimsSafely_differentSecretToken_returnsEmpty() {
        JwtProvider otherProvider = new JwtProvider();
        ReflectionTestUtils.setField(otherProvider, "secret",
            "other-secret-key-minimum-32-characters-long");
        ReflectionTestUtils.setField(otherProvider, "accessTokenExpiry", ACCESS_TOKEN_EXPIRY);
        ReflectionTestUtils.setField(otherProvider, "refreshTokenExpiry", REFRESH_TOKEN_EXPIRY);

        String tokenFromOther = otherProvider.issue(1L, Role.STUDENT.name()).accessToken();

        assertThat(jwtProvider.parseClaimsSafely(tokenFromOther)).isEmpty();
    }

    @Test
    @DisplayName("만료된 토큰은 Optional.empty를 반환한다")
    void parseClaimsSafely_expiredToken_returnsEmpty() {
        JwtProvider expiredProvider = new JwtProvider();
        ReflectionTestUtils.setField(expiredProvider, "secret", SECRET);
        ReflectionTestUtils.setField(expiredProvider, "accessTokenExpiry", -1L);
        ReflectionTestUtils.setField(expiredProvider, "refreshTokenExpiry", REFRESH_TOKEN_EXPIRY);

        String expiredToken = expiredProvider.issue(1L, Role.STUDENT.name()).accessToken();

        assertThat(jwtProvider.parseClaimsSafely(expiredToken)).isEmpty();
    }
}
