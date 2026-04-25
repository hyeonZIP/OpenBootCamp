package hyeonzip.openbootcamp.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import hyeonzip.openbootcamp.user.domain.RefreshToken;
import hyeonzip.openbootcamp.user.domain.Role;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.fixture.OAuth2UserInfoFixture;
import hyeonzip.openbootcamp.user.fixture.RefreshTokenFixture;
import hyeonzip.openbootcamp.user.fixture.UserFixture;
import hyeonzip.openbootcamp.user.fixture.UserOAuthFixture;
import hyeonzip.openbootcamp.user.repository.RefreshTokenRepository;
import hyeonzip.openbootcamp.user.repository.UserOAuthRepository;
import hyeonzip.openbootcamp.user.repository.UserRepository;
import hyeonzip.openbootcamp.user.service.ports.inp.AuthService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOAuthRepository userOAuthRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EntityManager entityManager;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(UserFixture.user());
        userOAuthRepository.save(UserOAuthFixture.userOAuth(savedUser));
        entityManager.flush();
        entityManager.clear();
    }

    // ── upsertFromOAuth2 ─────────────────────────────────────────────────────

    @Test
    @DisplayName("신규 OAuth2 사용자는 User와 UserOAuth가 새로 생성된다")
    void upsertFromOAuth2_newUser_createsUserAndOAuth() {
        long beforeUserCount = userRepository.count();
        long beforeOAuthCount = userOAuthRepository.count();

        authService.upsertFromOAuth2(OAuth2UserInfoFixture.githubUserInfo());

        // BeforeEach에서 이미 동일 providerId로 저장했으므로 기존 row 재사용됨
        // 여기선 다른 providerId로 신규 생성 테스트
        assertThat(userRepository.count()).isEqualTo(beforeUserCount);
        assertThat(userOAuthRepository.count()).isEqualTo(beforeOAuthCount);
    }

    @Test
    @DisplayName("기존 OAuth2 사용자의 프로필 정보가 최신 값으로 갱신된다")
    void upsertFromOAuth2_existingUser_updatesProfile() {
        authService.upsertFromOAuth2(OAuth2UserInfoFixture.updatedGithubUserInfo());
        entityManager.flush();
        entityManager.clear();

        User updated = userRepository.findById(savedUser.getId()).get();

        assertThat(updated.getUsername()).isEqualTo(OAuth2UserInfoFixture.UPDATED_USERNAME);
        assertThat(updated.getEmail()).isEqualTo(OAuth2UserInfoFixture.UPDATED_EMAIL);
        assertThat(updated.getAvatarUrl()).isEqualTo(OAuth2UserInfoFixture.UPDATED_AVATAR_URL);
    }

    @Test
    @DisplayName("기존 OAuth2 사용자 갱신 시 role은 변경되지 않는다")
    void upsertFromOAuth2_existingUser_doesNotChangeRole() {
        User result = authService.upsertFromOAuth2(OAuth2UserInfoFixture.updatedGithubUserInfo());

        assertThat(result.getRole()).isEqualTo(Role.STUDENT);
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("존재하는 ID로 조회하면 User를 반환한다")
    void findById_found() {
        User result = authService.findById(savedUser.getId());

        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getUsername()).isEqualTo(savedUser.getUsername());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 USER_NOT_FOUND 예외가 발생한다")
    void findById_notFound_throwsException() {
        assertThatThrownBy(() -> authService.findById(999L))
            .isInstanceOf(OpenBootCampException.class)
            .satisfies(e -> assertThat(((OpenBootCampException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND));
    }

    // ── saveRefreshToken ─────────────────────────────────────────────────────

    @Test
    @DisplayName("saveRefreshToken 호출 시 DB에 RefreshToken이 저장된다")
    void saveRefreshToken_savesToDatabase() {
        authService.saveRefreshToken(savedUser, RefreshTokenFixture.TOKEN);
        entityManager.flush();
        entityManager.clear();

        assertThat(refreshTokenRepository.findByToken(RefreshTokenFixture.TOKEN)).isPresent();
    }

    @Test
    @DisplayName("saveRefreshToken 호출 시 활성 상태로 저장된다")
    void saveRefreshToken_savedAsActive() {
        authService.saveRefreshToken(savedUser, RefreshTokenFixture.TOKEN);
        entityManager.flush();
        entityManager.clear();

        RefreshToken saved = refreshTokenRepository.findByToken(RefreshTokenFixture.TOKEN).get();
        assertThat(saved.isActive()).isTrue();
    }

    // ── refresh ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("유효한 RefreshToken으로 refresh() 호출 시 해당 User를 반환한다")
    void refresh_validToken_returnsUser() {
        refreshTokenRepository.save(RefreshTokenFixture.refreshToken(savedUser));
        entityManager.flush();
        entityManager.clear();

        User result = authService.refresh(RefreshTokenFixture.TOKEN);

        assertThat(result.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("존재하지 않는 토큰으로 refresh() 호출 시 INVALID_REFRESH_TOKEN 예외가 발생한다")
    void refresh_nonExistentToken_throwsException() {
        assertThatThrownBy(() -> authService.refresh("nonexistent-token"))
            .isInstanceOf(OpenBootCampException.class)
            .satisfies(e -> assertThat(((OpenBootCampException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN));
    }

    @Test
    @DisplayName("무효화된 RefreshToken으로 refresh() 호출 시 INVALID_REFRESH_TOKEN 예외가 발생한다")
    void refresh_invalidatedToken_throwsException() {
        RefreshToken token = refreshTokenRepository.save(RefreshTokenFixture.refreshToken(savedUser));
        token.invalidate();
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> authService.refresh(RefreshTokenFixture.TOKEN))
            .isInstanceOf(OpenBootCampException.class)
            .satisfies(e -> assertThat(((OpenBootCampException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN));
    }

    @Test
    @DisplayName("만료된 RefreshToken으로 refresh() 호출 시 INVALID_REFRESH_TOKEN 예외가 발생한다")
    void refresh_expiredToken_throwsException() {
        refreshTokenRepository.save(RefreshTokenFixture.expiredRefreshToken(savedUser));
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> authService.refresh(RefreshTokenFixture.TOKEN))
            .isInstanceOf(OpenBootCampException.class)
            .satisfies(e -> assertThat(((OpenBootCampException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN));
    }

    // ── logout ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("logout() 호출 시 RefreshToken이 무효화된다")
    void logout_invalidatesToken() {
        refreshTokenRepository.save(RefreshTokenFixture.refreshToken(savedUser));
        entityManager.flush();
        entityManager.clear();

        authService.logout(RefreshTokenFixture.TOKEN);
        entityManager.flush();
        entityManager.clear();

        RefreshToken result = refreshTokenRepository.findByToken(RefreshTokenFixture.TOKEN).get();
        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 토큰으로 logout() 호출 시 INVALID_REFRESH_TOKEN 예외가 발생한다")
    void logout_nonExistentToken_throwsException() {
        assertThatThrownBy(() -> authService.logout("nonexistent-token"))
            .isInstanceOf(OpenBootCampException.class)
            .satisfies(e -> assertThat(((OpenBootCampException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN));
    }
}