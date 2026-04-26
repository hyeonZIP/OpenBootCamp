package hyeonzip.openbootcamp.common.security.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.common.security.jwt.TokenPair;
import jakarta.servlet.http.Cookie;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

class CookieProviderTest {

    private CookieProvider cookieProvider;

    private static final long ACCESS_TOKEN_EXPIRY = 900_000L;
    private static final long REFRESH_TOKEN_EXPIRY = 604_800_000L;
    private static final TokenPair TOKEN_PAIR =
        new TokenPair("sample-access-token", "sample-refresh-token");

    @BeforeEach
    void setUp() {
        cookieProvider = new CookieProvider();
        ReflectionTestUtils.setField(cookieProvider, "accessTokenExpiry", ACCESS_TOKEN_EXPIRY);
        ReflectionTestUtils.setField(cookieProvider, "refreshTokenExpiry", REFRESH_TOKEN_EXPIRY);
    }

    // ── extractAccessToken ────────────────────────────────────────

    @Test
    @DisplayName("accessToken 쿠키가 있으면 토큰 값을 반환한다")
    void extractAccessToken_present_returnsToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", "some-token"));

        assertThat(cookieProvider.extractAccessToken(request)).contains("some-token");
    }

    @Test
    @DisplayName("쿠키가 없으면 빈 Optional을 반환한다")
    void extractAccessToken_noCookies_returnsEmpty() {
        assertThat(cookieProvider.extractAccessToken(new MockHttpServletRequest())).isEmpty();
    }

    @Test
    @DisplayName("accessToken이 아닌 다른 이름의 쿠키는 무시한다")
    void extractAccessToken_wrongName_returnsEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", "some-token"));

        assertThat(cookieProvider.extractAccessToken(request)).isEmpty();
    }

    // ── extractRefreshToken ───────────────────────────────────────

    @Test
    @DisplayName("refreshToken 쿠키가 있으면 토큰 값을 반환한다")
    void extractRefreshToken_present_returnsToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", "some-rt"));

        assertThat(cookieProvider.extractRefreshToken(request)).contains("some-rt");
    }

    @Test
    @DisplayName("refreshToken 쿠키가 없으면 빈 Optional을 반환한다")
    void extractRefreshToken_noCookies_returnsEmpty() {
        assertThat(cookieProvider.extractRefreshToken(new MockHttpServletRequest())).isEmpty();
    }

    @Test
    @DisplayName("refreshToken이 아닌 다른 이름의 쿠키는 무시한다")
    void extractRefreshToken_wrongName_returnsEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", "some-token"));

        assertThat(cookieProvider.extractRefreshToken(request)).isEmpty();
    }

    // ── addAccessTokenCookie ──────────────────────────────────────

    @Test
    @DisplayName("addAccessTokenCookie는 accessToken을 HttpOnly Set-Cookie 헤더로 추가한다")
    void addAccessTokenCookie_addsHttpOnlyCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieProvider.addAccessTokenCookie(response, "new-access-token");

        assertThat(setCookies(response))
            .anyMatch(c -> c.startsWith("accessToken=new-access-token") && c.contains("HttpOnly"));
    }

    // ── addTokenCookies ───────────────────────────────────────────

    @Test
    @DisplayName("accessToken이 HttpOnly Set-Cookie 헤더로 추가된다")
    void addTokenCookies_addsAccessTokenCookieWithHttpOnly() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieProvider.addTokenCookies(response, TOKEN_PAIR);

        assertThat(setCookies(response))
            .anyMatch(c -> c.startsWith("accessToken=") && c.contains("HttpOnly"));
    }

    @Test
    @DisplayName("refreshToken이 /api/v1/auth 경로로 제한된 HttpOnly 쿠키로 추가된다")
    void addTokenCookies_addsRefreshTokenCookieWithRestrictedPath() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieProvider.addTokenCookies(response, TOKEN_PAIR);

        assertThat(setCookies(response))
            .anyMatch(c -> c.startsWith("refreshToken=")
                && c.contains("HttpOnly")
                && c.contains("Path=/api/v1/auth"));
    }

    // ── clearTokenCookies ─────────────────────────────────────────

    @Test
    @DisplayName("clearTokenCookies는 accessToken과 refreshToken을 Max-Age=0으로 만료시킨다")
    void clearTokenCookies_expiresBothCookies() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieProvider.clearTokenCookies(response);

        Collection<String> cookies = setCookies(response);
        assertThat(cookies).anyMatch(c -> c.startsWith("accessToken=") && c.contains("Max-Age=0"));
        assertThat(cookies).anyMatch(c -> c.startsWith("refreshToken=") && c.contains("Max-Age=0"));
    }

    private Collection<String> setCookies(MockHttpServletResponse response) {
        return response.getHeaders("Set-Cookie");
    }
}
