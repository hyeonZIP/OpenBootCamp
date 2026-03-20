package hyeonzip.openbootcamp.user.fixture;

import hyeonzip.openbootcamp.user.domain.Role;
import hyeonzip.openbootcamp.user.dto.UserRequest;

public final class UserRequestFixture {

    public static final String GITHUB_ID = "octocat";
    public static final String USERNAME = "Octocat";
    public static final String EMAIL = "octocat@github.com";
    public static final String AVATAR_URL = "https://avatars.githubusercontent.com/u/583231";
    public static final Role ROLE = Role.STUDENT;

    public static final String OTHER_USERNAME = "monalisa";
    public static final String OTHER_EMAIL = "monalisa@github.com";
    public static final String OTHER_AVATAR_URL = "https://avatars.githubusercontent.com/u/9919";

    private UserRequestFixture() {
    }

    public static UserRequest createRequest() {
        return new UserRequest(GITHUB_ID, USERNAME, EMAIL, AVATAR_URL, ROLE);
    }

    public static UserRequest updateProfileRequest() {
        return new UserRequest(GITHUB_ID, OTHER_USERNAME, OTHER_EMAIL, OTHER_AVATAR_URL, ROLE);
    }

    public static UserRequest changeRoleRequest(Role role) {
        return new UserRequest(GITHUB_ID, USERNAME, EMAIL, AVATAR_URL, role);
    }
}
