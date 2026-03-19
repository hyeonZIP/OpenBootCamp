package hyeonzip.openbootcamp.user.fixture;

import static hyeonzip.openbootcamp.user.fixture.UserRequestFixture.AVATAR_URL;
import static hyeonzip.openbootcamp.user.fixture.UserRequestFixture.EMAIL;
import static hyeonzip.openbootcamp.user.fixture.UserRequestFixture.GITHUB_ID;
import static hyeonzip.openbootcamp.user.fixture.UserRequestFixture.ROLE;
import static hyeonzip.openbootcamp.user.fixture.UserRequestFixture.USERNAME;

import hyeonzip.openbootcamp.user.domain.User;

public final class UserFixture {

    private UserFixture() {
    }

    public static User user() {
        return User.create(GITHUB_ID, USERNAME, EMAIL, AVATAR_URL, ROLE);
    }
}
