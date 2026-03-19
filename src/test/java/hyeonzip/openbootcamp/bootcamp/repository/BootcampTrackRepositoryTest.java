package hyeonzip.openbootcamp.bootcamp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackFixture;
import hyeonzip.openbootcamp.common.config.JpaAuditingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class BootcampTrackRepositoryTest {

    private static final Long NON_EXISTING_BOOTCAMP_ID = 999L;

    @Autowired
    private BootcampTrackRepository bootcampTrackRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Bootcamp savedBootcamp;
    private BootcampTrack savedBootcampTrack;

    @BeforeEach
    void setUp() {
        savedBootcamp = BootcampFixture.bootcamp();
        savedBootcampTrack = BootcampTrackFixture.bootcampTrack();
        savedBootcamp.addTrack(savedBootcampTrack);

        testEntityManager.persistAndFlush(savedBootcamp);
    }

    @Test
    @DisplayName("부트캠프 ID와 트랙 ID가 일치하면 트랙을 반환한다")
    void findByIdAndBootcampId_returnsTrack_whenBothMatch() {
        var result = bootcampTrackRepository.findByIdAndBootcampId(savedBootcampTrack.getId(),
            savedBootcamp.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTrackType()).isEqualTo(savedBootcampTrack.getTrackType());
    }

    @Test
    @DisplayName("부트캠프 ID가 다르면 빈 Optional을 반환한다")
    void findByIdAndBootcampId_returnsEmpty_whenBootcampIdMismatch() {
        var result = bootcampTrackRepository.findByIdAndBootcampId(savedBootcampTrack.getId(),
            NON_EXISTING_BOOTCAMP_ID);

        assertThat(result).isEmpty();
    }
}
