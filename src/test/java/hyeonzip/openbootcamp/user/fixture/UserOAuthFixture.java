package hyeonzip.openbootcamp.user.fixture;

import hyeonzip.openbootcamp.user.domain.OAuthProvider;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.domain.UserOAuth;

public final class UserOAuthFixture {

    public static final OAuthProvider PROVIDER = OAuthProvider.GITHUB;
    public static final String PROVIDER_ID = "12345678";

    public static final OAuthProvider OTHER_PROVIDER = OAuthProvider.KAKAO;
    public static final String OTHER_PROVIDER_ID = "87654321";

    private UserOAuthFixture() {
    }

    public static UserOAuth userOAuth(User user) {
        return UserOAuth.create(user, PROVIDER, PROVIDER_ID);
    }

    public static UserOAuth userOAuth(User user, OAuthProvider provider, String providerId) {
        return UserOAuth.create(user, provider, providerId);
    }
}
