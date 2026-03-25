package hyeonzip.openbootcamp.common.security.oauth2;

import hyeonzip.openbootcamp.common.security.oauth2.userinfo.OAuth2UserInfo;
import hyeonzip.openbootcamp.user.domain.Role;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserInfo userInfo;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(OAuth2UserInfo userInfo, Map<String, Object> attributes) {
        this.userInfo = userInfo;
        this.attributes = Map.copyOf(attributes);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.STUDENT.getAuthority()));
    }

    @Override
    public String getName() {
        return userInfo.getId();
    }
}
