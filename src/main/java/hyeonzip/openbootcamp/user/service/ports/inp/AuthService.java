package hyeonzip.openbootcamp.user.service.ports.inp;

import hyeonzip.openbootcamp.common.security.oauth2.userinfo.OAuth2UserInfo;
import hyeonzip.openbootcamp.user.domain.User;

public interface AuthService {

    User upsertFromOAuth2(OAuth2UserInfo userInfo);

    User findById(Long id);

    void saveRefreshToken(User user, String tokenValue);

    User refresh(String tokenValue);

    void logout(String tokenValue);
}
