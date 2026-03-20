package hyeonzip.openbootcamp.common.security.oauth2;

import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import java.util.Map;

public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        if ("github".equalsIgnoreCase(registrationId)) {
            return new GithubOAuth2UserInfo(attributes);
        }
        // TODO: 카카오, 구글 등 추가 시 여기에 분기 추가
        throw new OpenBootCampException(ErrorCode.UNSUPPORTED_OAUTH2_PROVIDER);
    }
}
