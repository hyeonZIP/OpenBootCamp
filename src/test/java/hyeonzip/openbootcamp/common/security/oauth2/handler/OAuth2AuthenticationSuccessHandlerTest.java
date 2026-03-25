package hyeonzip.openbootcamp.common.security.oauth2.handler;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.common.security.oauth2.CustomOAuth2User;
import hyeonzip.openbootcamp.common.security.oauth2.userinfo.GithubOAuth2UserInfo;
import hyeonzip.openbootcamp.user.domain.OAuthProvider;
import hyeonzip.openbootcamp.user.domain.UserOAuth;
import hyeonzip.openbootcamp.user.repository.UserOAuthRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OAuth2AuthenticationSuccessHandlerTest {

    @Autowired
    private OAuth2AuthenticationSuccessHandler handler;

    @Autowired
    private UserOAuthRepository userOAuthRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private static final Map<String, Object> GITHUB_ATTRIBUTES = Map.of(
        "id", 12345678,
        "login", "octocat",
        "email", "octocat@github.com",
        "avatar_url", "https://avatars.githubusercontent.com/u/12345678"
    );

    private OAuth2AuthenticationToken buildAuth(Map<String, Object> attributes) {
        GithubOAuth2UserInfo userInfo = new GithubOAuth2UserInfo(attributes);
        CustomOAuth2User customUser = new CustomOAuth2User(userInfo, attributes);
        return new OAuth2AuthenticationToken(customUser, customUser.getAuthorities(), "github");
    }

    // ── DB 상태 ──────────────────────────────────────────────────

    @Test
    @DisplayName("신규 사용자 로그인 시 User와 UserOAuth가 DB에 저장된다")
    void onAuthSuccess_newUser_savesUserAndUserOAuth() throws Exception {
        handler.onAuthenticationSuccess(new MockHttpServletRequest(),
            new MockHttpServletResponse(), buildAuth(GITHUB_ATTRIBUTES));

        assertThat(userOAuthRepository.findByProviderAndProviderId(
            OAuthProvider.GITHUB, "12345678")).isPresent();
    }

    @Test
    @DisplayName("기존 사용자 재로그인 시 User는 새로 생성되지 않고 프로필이 업데이트된다")
    void onAuthSuccess_existingUser_updatesProfileWithoutDuplication() throws Exception {
        handler.onAuthenticationSuccess(new MockHttpServletRequest(),
            new MockHttpServletResponse(), buildAuth(GITHUB_ATTRIBUTES));

        Map<String, Object> updatedAttributes = Map.of(
            "id", 12345678,
            "login", "octocat-updated",
            "email", "updated@github.com",
            "avatar_url", "https://avatars.githubusercontent.com/u/updated"
        );
        handler.onAuthenticationSuccess(new MockHttpServletRequest(),
            new MockHttpServletResponse(), buildAuth(updatedAttributes));

        var user = userOAuthRepository
            .findByProviderAndProviderId(OAuthProvider.GITHUB, "12345678")
            .map(UserOAuth::getUser)
            .orElseThrow();

        assertThat(user.getUsername()).isEqualTo("octocat-updated");
        assertThat(userOAuthRepository.findByProviderAndProviderId(
            OAuthProvider.GITHUB, "12345678")).isPresent();
    }

    // ── 리다이렉트 ────────────────────────────────────────────────

    @Test
    @DisplayName("인증 성공 시 frontendUrl + /auth/callback 으로 리다이렉트한다")
    void onAuthSuccess_redirectsToFrontendCallback() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(new MockHttpServletRequest(), response,
            buildAuth(GITHUB_ATTRIBUTES));

        assertThat(response.getRedirectedUrl()).isEqualTo(frontendUrl + "/auth/callback");
    }

    // ── 쿠키 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("accessToken 쿠키가 HttpOnly로 Set-Cookie 헤더에 포함된다")
    void onAuthSuccess_setsAccessTokenCookieWithHttpOnly() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(new MockHttpServletRequest(), response,
            buildAuth(GITHUB_ATTRIBUTES));

        List<String> accessCookies = response.getHeaders("Set-Cookie").stream()
            .filter(c -> c.startsWith("accessToken="))
            .toList();
        assertThat(accessCookies).hasSize(1);
        assertThat(accessCookies.getFirst()).contains("HttpOnly");
    }

    @Test
    @DisplayName("refreshToken 쿠키가 HttpOnly이며 /api/v1/auth/refresh 경로로 제한된다")
    void onAuthSuccess_setsRefreshTokenCookieWithRestrictedPath() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(new MockHttpServletRequest(), response,
            buildAuth(GITHUB_ATTRIBUTES));

        List<String> refreshCookies = response.getHeaders("Set-Cookie").stream()
            .filter(c -> c.startsWith("refreshToken="))
            .toList();
        assertThat(refreshCookies).hasSize(1);
        assertThat(refreshCookies.getFirst())
            .contains("HttpOnly")
            .contains("Path=/api/v1/auth/refresh");
    }
}
