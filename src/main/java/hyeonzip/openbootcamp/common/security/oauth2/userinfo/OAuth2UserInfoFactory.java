package hyeonzip.openbootcamp.common.security.oauth2.userinfo;

import hyeonzip.openbootcamp.user.domain.OAuthProvider;
import java.util.Map;

public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        OAuthProvider provider = OAuthProvider.from(registrationId);

        return switch (provider) {
            case GITHUB -> new GithubOAuth2UserInfo(attributes);
            case KAKAO, GOOGLE -> throw new UnsupportedOperationException(
                provider.name() + " 로그인은 아직 지원하지 않습니다.");
        };
    }
}
