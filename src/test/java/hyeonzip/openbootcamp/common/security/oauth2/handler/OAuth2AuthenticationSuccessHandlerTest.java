package hyeonzip.openbootcamp.common.security.oauth2.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hyeonzip.openbootcamp.common.security.cookie.CookieProvider;
import hyeonzip.openbootcamp.common.security.jwt.JwtProvider;
import hyeonzip.openbootcamp.common.security.jwt.TokenPair;
import hyeonzip.openbootcamp.common.security.oauth2.CustomOAuth2User;
import hyeonzip.openbootcamp.common.security.oauth2.userinfo.GithubOAuth2UserInfo;
import hyeonzip.openbootcamp.common.security.oauth2.userinfo.OAuth2UserInfo;
import hyeonzip.openbootcamp.user.domain.OAuthProvider;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.fixture.UserFixture;
import hyeonzip.openbootcamp.user.fixture.UserOAuthFixture;
import hyeonzip.openbootcamp.user.fixture.UserRequestFixture;
import hyeonzip.openbootcamp.user.service.ports.inp.AuthService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CookieProvider cookieProvider;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler handler;

    private static final String FRONTEND_URL = "http://localhost:3000";

    private static final Map<String, Object> GITHUB_ATTRIBUTES = Map.of(
        "id", UserOAuthFixture.PROVIDER_ID,
        "login", UserRequestFixture.USERNAME,
        "email", UserRequestFixture.EMAIL,
        "avatar_url", UserRequestFixture.AVATAR_URL
    );

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private OAuth2AuthenticationToken githubAuth;
    private User stubUser;
    private TokenPair stubTokenPair;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "frontendUrl", FRONTEND_URL);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        githubAuth = buildAuth(GITHUB_ATTRIBUTES);

        stubUser = UserFixture.user();
        stubTokenPair = new TokenPair("stub-access-token", "stub-refresh-token");

        when(authService.upsertFromOAuth2(any(OAuth2UserInfo.class))).thenReturn(stubUser);
        when(jwtProvider.issue(stubUser.getId(), stubUser.getRole().name())).thenReturn(
            stubTokenPair);
    }

    private OAuth2AuthenticationToken buildAuth(Map<String, Object> attributes) {
        GithubOAuth2UserInfo userInfo = new GithubOAuth2UserInfo(attributes);
        CustomOAuth2User customUser = new CustomOAuth2User(userInfo, attributes);
        return new OAuth2AuthenticationToken(
            customUser, customUser.getAuthorities(), OAuthProvider.GITHUB.getRegistrationId());
    }

    // ── 서비스 위임 ──────────────────────────────────────────────────

    @Test
    @DisplayName("OAuth2 유저 정보로 upsertFromOAuth2를 호출한다")
    void onAuthSuccess_callsUpsertWithUserInfo() throws Exception {
        OAuth2UserInfo expectedUserInfo = ((CustomOAuth2User) githubAuth.getPrincipal()).getUserInfo();

        handler.onAuthenticationSuccess(request, response, githubAuth);

        verify(authService).upsertFromOAuth2(expectedUserInfo);
    }

    @Test
    @DisplayName("upsertFromOAuth2가 반환한 User로 토큰을 발급한다")
    void onAuthSuccess_issuesTokenForReturnedUser() throws Exception {
        handler.onAuthenticationSuccess(request, response, githubAuth);

        verify(jwtProvider).issue(stubUser.getId(), stubUser.getRole().name());
    }

    @Test
    @DisplayName("발급된 TokenPair로 쿠키를 설정한다")
    void onAuthSuccess_addsCookiesWithIssuedTokenPair() throws Exception {
        handler.onAuthenticationSuccess(request, response, githubAuth);

        verify(cookieProvider).addTokenCookies(response, stubTokenPair);
    }

    // ── 리다이렉트 ────────────────────────────────────────────────

    @Test
    @DisplayName("인증 성공 시 frontendUrl + /auth/callback 으로 리다이렉트한다")
    void onAuthSuccess_redirectsToFrontendCallback() throws Exception {
        handler.onAuthenticationSuccess(request, response, githubAuth);

        assertThat(response.getRedirectedUrl()).isEqualTo(FRONTEND_URL + "/auth/callback");
    }
}
