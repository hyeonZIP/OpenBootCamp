package hyeonzip.openbootcamp.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.user.fixture.RefreshTokenFixture;
import hyeonzip.openbootcamp.user.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

    @Test
    @DisplayName("정적 팩토리 메서드로 RefreshToken을 생성하면 요청 값이 그대로 저장된다")
    void create_setsFieldsCorrectly() {
        User user = UserFixture.user();

        RefreshToken token = RefreshTokenFixture.refreshToken(user);

        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getToken()).isEqualTo(RefreshTokenFixture.TOKEN);
        assertThat(token.getExpiresAt()).isEqualTo(RefreshTokenFixture.EXPIRES_AT);
    }

    @Test
    @DisplayName("RefreshToken 생성 시 활성 상태(active=true)로 초기화된다")
    void create_initiallyActive() {
        RefreshToken token = RefreshTokenFixture.refreshToken(UserFixture.user());

        assertThat(token.isActive()).isTrue();
    }

    @Test
    @DisplayName("invalidate() 호출 시 비활성 상태(active=false)가 된다")
    void invalidate_setsActiveFalse() {
        RefreshToken token = RefreshTokenFixture.refreshToken(UserFixture.user());

        token.invalidate();

        assertThat(token.isActive()).isFalse();
    }

    @Test
    @DisplayName("invalidate() 호출 후 token, expiresAt은 변경되지 않는다")
    void invalidate_doesNotChangeOtherFields() {
        RefreshToken token = RefreshTokenFixture.refreshToken(UserFixture.user());

        token.invalidate();

        assertThat(token.getToken()).isEqualTo(RefreshTokenFixture.TOKEN);
        assertThat(token.getExpiresAt()).isEqualTo(RefreshTokenFixture.EXPIRES_AT);
    }

    @Test
    @DisplayName("만료 시각이 지나지 않은 토큰은 isExpired()가 false를 반환한다")
    void isExpired_returnsFalse_whenNotExpired() {
        RefreshToken token = RefreshTokenFixture.refreshToken(UserFixture.user());

        assertThat(token.isExpired()).isFalse();
    }

    @Test
    @DisplayName("만료 시각이 지난 토큰은 isExpired()가 true를 반환한다")
    void isExpired_returnsTrue_whenExpired() {
        RefreshToken token = RefreshTokenFixture.expiredRefreshToken(UserFixture.user());

        assertThat(token.isExpired()).isTrue();
    }

    @Test
    @DisplayName("활성 상태이고 만료되지 않은 토큰은 isValid()가 true를 반환한다")
    void isValid_returnsTrue_whenActiveAndNotExpired() {
        RefreshToken token = RefreshTokenFixture.refreshToken(UserFixture.user());

        assertThat(token.isValid()).isTrue();
    }

    @Test
    @DisplayName("invalidate() 호출 후 isValid()가 false를 반환한다")
    void isValid_returnsFalse_afterInvalidate() {
        RefreshToken token = RefreshTokenFixture.refreshToken(UserFixture.user());

        token.invalidate();

        assertThat(token.isValid()).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 isValid()가 false를 반환한다")
    void isValid_returnsFalse_whenExpired() {
        RefreshToken token = RefreshTokenFixture.expiredRefreshToken(UserFixture.user());

        assertThat(token.isValid()).isFalse();
    }
}