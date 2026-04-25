package hyeonzip.openbootcamp.user.fixture;

import hyeonzip.openbootcamp.common.security.oauth2.userinfo.GithubOAuth2UserInfo;
import hyeonzip.openbootcamp.common.security.oauth2.userinfo.OAuth2UserInfo;
import java.util.Map;

public final class OAuth2UserInfoFixture {

    public static final String PROVIDER_ID = UserOAuthFixture.PROVIDER_ID;
    public static final String USERNAME = UserRequestFixture.USERNAME;
    public static final String EMAIL = UserRequestFixture.EMAIL;
    public static final String AVATAR_URL = UserRequestFixture.AVATAR_URL;

    public static final String UPDATED_USERNAME = UserRequestFixture.OTHER_USERNAME;
    public static final String UPDATED_EMAIL = UserRequestFixture.OTHER_EMAIL;
    public static final String UPDATED_AVATAR_URL = UserRequestFixture.OTHER_AVATAR_URL;

    private OAuth2UserInfoFixture() {
    }

    public static OAuth2UserInfo githubUserInfo() {
        return new GithubOAuth2UserInfo(Map.of(
            "id", Integer.parseInt(PROVIDER_ID),
            "login", USERNAME,
            "email", EMAIL,
            "avatar_url", AVATAR_URL
        ));
    }

    public static OAuth2UserInfo updatedGithubUserInfo() {
        return new GithubOAuth2UserInfo(Map.of(
            "id", Integer.parseInt(PROVIDER_ID),
            "login", UPDATED_USERNAME,
            "email", UPDATED_EMAIL,
            "avatar_url", UPDATED_AVATAR_URL
        ));
    }
}