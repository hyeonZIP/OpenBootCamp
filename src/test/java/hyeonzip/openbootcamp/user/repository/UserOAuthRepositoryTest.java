package hyeonzip.openbootcamp.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.common.config.JpaAuditingConfig;
import hyeonzip.openbootcamp.user.domain.OAuthProvider;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.domain.UserOAuth;
import hyeonzip.openbootcamp.user.fixture.UserFixture;
import hyeonzip.openbootcamp.user.fixture.UserOAuthFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class UserOAuthRepositoryTest {

    @Autowired
    private UserOAuthRepository userOAuthRepository;

    @Autowired
    private TestEntityManager em;

    private User savedUser;
    private UserOAuth savedUserOAuth;

    @BeforeEach
    void setUp() {
        savedUser = em.persist(UserFixture.user());
        savedUserOAuth = em.persist(UserOAuthFixture.userOAuth(savedUser));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("provider와 providerId로 UserOAuth를 조회할 수 있다")
    void findByProviderAndProviderId_found() {
        Optional<UserOAuth> result = userOAuthRepository.findByProviderAndProviderId(
            UserOAuthFixture.PROVIDER, UserOAuthFixture.PROVIDER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getProvider()).isEqualTo(UserOAuthFixture.PROVIDER);
        assertThat(result.get().getProviderId()).isEqualTo(UserOAuthFixture.PROVIDER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 providerId로 조회 시 빈 Optional을 반환한다")
    void findByProviderAndProviderId_notFound() {
        Optional<UserOAuth> result = userOAuthRepository.findByProviderAndProviderId(
            UserOAuthFixture.PROVIDER, "nonexistent-id");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 provider로 조회 시 빈 Optional을 반환한다")
    void findByProviderAndProviderId_wrongProvider() {
        Optional<UserOAuth> result = userOAuthRepository.findByProviderAndProviderId(
            OAuthProvider.KAKAO, UserOAuthFixture.PROVIDER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조회된 UserOAuth는 연관된 User를 포함한다")
    void findByProviderAndProviderId_includesUser() {
        Optional<UserOAuth> result = userOAuthRepository.findByProviderAndProviderId(
            UserOAuthFixture.PROVIDER, UserOAuthFixture.PROVIDER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("같은 User에 서로 다른 provider로 각각 저장하고 조회할 수 있다")
    void findByProviderAndProviderId_differentProvidersSameUser() {
        UserOAuth kakaoOAuth = UserOAuthFixture.userOAuth(
            savedUser, OAuthProvider.KAKAO, UserOAuthFixture.OTHER_PROVIDER_ID);
        em.persist(kakaoOAuth);
        em.flush();
        em.clear();

        Optional<UserOAuth> github = userOAuthRepository.findByProviderAndProviderId(
            OAuthProvider.GITHUB, UserOAuthFixture.PROVIDER_ID);
        Optional<UserOAuth> kakao = userOAuthRepository.findByProviderAndProviderId(
            OAuthProvider.KAKAO, UserOAuthFixture.OTHER_PROVIDER_ID);

        assertThat(github).isPresent();
        assertThat(kakao).isPresent();
        assertThat(github.get().getUser().getId()).isEqualTo(kakao.get().getUser().getId());
    }

    @Test
    @DisplayName("UserOAuth를 저장하면 ID가 부여된다")
    void save_assignsId() {
        UserOAuth userOAuth = UserOAuth.create(savedUser, OAuthProvider.GOOGLE, "google-999");

        UserOAuth saved = userOAuthRepository.save(userOAuth);

        assertThat(saved.getId()).isNotNull();
    }
}
