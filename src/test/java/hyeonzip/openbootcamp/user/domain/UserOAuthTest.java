package hyeonzip.openbootcamp.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.user.fixture.UserFixture;
import hyeonzip.openbootcamp.user.fixture.UserOAuthFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserOAuthTest {

    @Test
    @DisplayName("정적 팩토리 메서드로 UserOAuth를 생성하면 요청 값이 그대로 저장된다")
    void create_setsFields() {
        User user = UserFixture.user();

        UserOAuth userOAuth = UserOAuth.create(user, UserOAuthFixture.PROVIDER,
            UserOAuthFixture.PROVIDER_ID);

        assertThat(userOAuth.getUser()).isEqualTo(user);
        assertThat(userOAuth.getProvider()).isEqualTo(UserOAuthFixture.PROVIDER);
        assertThat(userOAuth.getProviderId()).isEqualTo(UserOAuthFixture.PROVIDER_ID);
    }

    @Test
    @DisplayName("UserOAuth 생성 시 활성 상태(active=true)로 초기화된다")
    void create_initiallyActive() {
        User user = UserFixture.user();

        UserOAuth userOAuth = UserOAuthFixture.userOAuth(user);

        assertThat(userOAuth.isActive()).isTrue();
    }

    @Test
    @DisplayName("provider가 GITHUB인 UserOAuth를 생성할 수 있다")
    void create_withGithubProvider() {
        User user = UserFixture.user();

        UserOAuth userOAuth = UserOAuthFixture.userOAuth(user, OAuthProvider.GITHUB, "111");

        assertThat(userOAuth.getProvider()).isEqualTo(OAuthProvider.GITHUB);
    }

    @Test
    @DisplayName("provider가 KAKAO인 UserOAuth를 생성할 수 있다")
    void create_withKakaoProvider() {
        User user = UserFixture.user();

        UserOAuth userOAuth = UserOAuthFixture.userOAuth(user, OAuthProvider.KAKAO, "222");

        assertThat(userOAuth.getProvider()).isEqualTo(OAuthProvider.KAKAO);
    }

    @Test
    @DisplayName("provider가 GOOGLE인 UserOAuth를 생성할 수 있다")
    void create_withGoogleProvider() {
        User user = UserFixture.user();

        UserOAuth userOAuth = UserOAuthFixture.userOAuth(user, OAuthProvider.GOOGLE, "333");

        assertThat(userOAuth.getProvider()).isEqualTo(OAuthProvider.GOOGLE);
    }

    @Test
    @DisplayName("같은 User에 서로 다른 provider로 UserOAuth를 생성할 수 있다")
    void create_differentProvidersForSameUser() {
        User user = UserFixture.user();

        UserOAuth github = UserOAuthFixture.userOAuth(user, OAuthProvider.GITHUB, "111");
        UserOAuth kakao = UserOAuthFixture.userOAuth(user, OAuthProvider.KAKAO, "222");

        assertThat(github.getProvider()).isNotEqualTo(kakao.getProvider());
        assertThat(github.getUser()).isEqualTo(kakao.getUser());
    }
}
