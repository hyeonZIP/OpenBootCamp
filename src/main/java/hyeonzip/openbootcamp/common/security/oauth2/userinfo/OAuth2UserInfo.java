package hyeonzip.openbootcamp.common.security.oauth2.userinfo;

import hyeonzip.openbootcamp.user.domain.OAuthProvider;
import java.util.Map;

public abstract class OAuth2UserInfo {

    protected final Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = Map.copyOf(attributes);
    }

    public abstract OAuthProvider getProvider();

    public abstract String getId();

    public abstract String getUsername();

    public abstract String getEmail();

    public abstract String getAvatarUrl();
}
