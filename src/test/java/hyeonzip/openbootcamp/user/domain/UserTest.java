package hyeonzip.openbootcamp.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.user.fixture.UserFixture;
import hyeonzip.openbootcamp.user.fixture.UserRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("정적 팩토리 메서드로 User를 생성하면 요청 값이 그대로 저장된다")
    void create_setsFieldsFromRequest() {
        var request = UserRequestFixture.createRequest();

        User user = User.create(request.username(), request.email(),
            request.avatarUrl(), request.role());

        assertThat(user.getUsername()).isEqualTo(request.username());
        assertThat(user.getEmail()).isEqualTo(request.email());
        assertThat(user.getAvatarUrl()).isEqualTo(request.avatarUrl());
        assertThat(user.getRole()).isEqualTo(request.role());
    }

    @Test
    @DisplayName("updateProfile() 호출 시 username, email, avatarUrl이 갱신된다")
    void updateProfile_updatesFields() {
        User user = UserFixture.user();
        var request = UserRequestFixture.updateProfileRequest();

        user.updateProfile(request.username(), request.email(), request.avatarUrl());

        assertThat(user.getUsername()).isEqualTo(request.username());
        assertThat(user.getEmail()).isEqualTo(request.email());
        assertThat(user.getAvatarUrl()).isEqualTo(request.avatarUrl());
    }

    @Test
    @DisplayName("updateProfile() 호출 후 role은 변경되지 않는다")
    void updateProfile_doesNotChangeRole() {
        User user = UserFixture.user();
        var request = UserRequestFixture.updateProfileRequest();

        user.updateProfile(request.username(), request.email(), request.avatarUrl());

        assertThat(user.getRole()).isEqualTo(UserRequestFixture.ROLE);
    }

    @Test
    @DisplayName("changeRole() 호출 시 role이 변경된다")
    void changeRole_updatesRole() {
        User user = UserFixture.user();

        user.changeRole(Role.ADMIN);

        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("changeRole() 호출 후 username, email은 변경되지 않는다")
    void changeRole_doesNotChangeOtherFields() {
        User user = UserFixture.user();

        user.changeRole(Role.BOOTCAMP_ADMIN);

        assertThat(user.getUsername()).isEqualTo(UserRequestFixture.USERNAME);
        assertThat(user.getEmail()).isEqualTo(UserRequestFixture.EMAIL);
    }

    @Test
    @DisplayName("updateProfile() 호출 시 null 값으로도 갱신할 수 있다")
    void updateProfile_withNullValues() {
        User user = UserFixture.user();

        user.updateProfile(UserRequestFixture.OTHER_USERNAME, null, null);

        assertThat(user.getUsername()).isEqualTo(UserRequestFixture.OTHER_USERNAME);
        assertThat(user.getEmail()).isNull();
        assertThat(user.getAvatarUrl()).isNull();
    }

    @Test
    @DisplayName("User 생성 시 활성 상태(active=true)로 초기화된다")
    void create_initiallyActive() {
        User user = UserFixture.user();

        assertThat(user.isActive()).isTrue();
    }

    @Test
    @DisplayName("deactivate() 호출 시 비활성 상태가 된다")
    void deactivate_setsActiveFalse() {
        User user = UserFixture.user();

        user.deactivate();

        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("activate() 호출 시 활성 상태가 된다")
    void activate_setsActiveTrue() {
        User user = UserFixture.user();
        user.deactivate();

        user.activate();

        assertThat(user.isActive()).isTrue();
    }

    @Test
    @DisplayName("deactivate() 호출 후 username, role은 변경되지 않는다")
    void deactivate_doesNotChangeOtherFields() {
        User user = UserFixture.user();

        user.deactivate();

        assertThat(user.getUsername()).isEqualTo(UserRequestFixture.USERNAME);
        assertThat(user.getRole()).isEqualTo(UserRequestFixture.ROLE);
    }
}
