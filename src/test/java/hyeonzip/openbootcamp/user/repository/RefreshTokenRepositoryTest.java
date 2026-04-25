package hyeonzip.openbootcamp.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.common.config.JpaAuditingConfig;
import hyeonzip.openbootcamp.user.domain.RefreshToken;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.fixture.RefreshTokenFixture;
import hyeonzip.openbootcamp.user.fixture.UserFixture;
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
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager em;

    private User savedUser;
    private RefreshToken savedToken;

    @BeforeEach
    void setUp() {
        savedUser = em.persist(UserFixture.user());
        savedToken = em.persist(RefreshTokenFixture.refreshToken(savedUser));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("저장된 토큰 문자열로 RefreshToken을 조회할 수 있다")
    void findByToken_found() {
        Optional<RefreshToken> result = refreshTokenRepository.findByToken(RefreshTokenFixture.TOKEN);

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo(RefreshTokenFixture.TOKEN);
    }

    @Test
    @DisplayName("존재하지 않는 토큰으로 조회 시 빈 Optional을 반환한다")
    void findByToken_notFound() {
        Optional<RefreshToken> result = refreshTokenRepository.findByToken("nonexistent-token");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조회된 RefreshToken은 연관된 User를 포함한다")
    void findByToken_includesUser() {
        Optional<RefreshToken> result = refreshTokenRepository.findByToken(RefreshTokenFixture.TOKEN);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("RefreshToken을 저장하면 ID가 부여된다")
    void save_assignsId() {
        RefreshToken token = RefreshToken.create(savedUser, "another-token", RefreshTokenFixture.EXPIRES_AT);

        RefreshToken saved = refreshTokenRepository.save(token);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("invalidate() 후 저장하면 active=false로 조회된다")
    void findByToken_afterInvalidate_isInactive() {
        RefreshToken token = refreshTokenRepository.findByToken(RefreshTokenFixture.TOKEN).get();
        token.invalidate();
        refreshTokenRepository.save(token);
        em.flush();
        em.clear();

        RefreshToken result = refreshTokenRepository.findByToken(RefreshTokenFixture.TOKEN).get();

        assertThat(result.isActive()).isFalse();
    }
}