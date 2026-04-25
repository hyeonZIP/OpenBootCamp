package hyeonzip.openbootcamp.user.fixture;

import hyeonzip.openbootcamp.user.domain.RefreshToken;
import hyeonzip.openbootcamp.user.domain.User;
import java.time.LocalDateTime;

public final class RefreshTokenFixture {

    public static final String TOKEN = "sample-refresh-token";
    public static final LocalDateTime EXPIRES_AT = LocalDateTime.now().plusDays(7);
    public static final LocalDateTime EXPIRED_AT = LocalDateTime.now().minusSeconds(1);

    private RefreshTokenFixture() {
    }

    public static RefreshToken refreshToken(User user) {
        return RefreshToken.create(user, TOKEN, EXPIRES_AT);
    }

    public static RefreshToken expiredRefreshToken(User user) {
        return RefreshToken.create(user, TOKEN, EXPIRED_AT);
    }
}