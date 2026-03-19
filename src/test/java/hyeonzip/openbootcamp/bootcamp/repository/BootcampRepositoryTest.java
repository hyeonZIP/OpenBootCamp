package hyeonzip.openbootcamp.bootcamp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackFixture;
import hyeonzip.openbootcamp.common.config.JpaAuditingConfig;
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
class BootcampRepositoryTest {

    private static final Long NON_EXISTING_BOOTCAMP_ID = 999L;
    private static final String NON_EXISTING_SLUG_VALUE = "non existing slug";

    @Autowired
    private BootcampRepository bootcampRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Bootcamp savedBootcamp;

    @BeforeEach
    void setUp() {
        savedBootcamp = testEntityManager.persistAndFlush(BootcampFixture.bootcamp());
    }

    @Test
    @DisplayName("존재하는 이름 조회 시 true를 반환한다")
    void existsByName_returnsTrue_whenExists() {
        assertThat(bootcampRepository.existsByName(savedBootcamp.getName())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이름 조회 시 false를 반환한다")
    void existsByName_returnsFalse_whenNotExists() {
        assertThat(bootcampRepository.existsByName("존재하지 않는 이름")).isFalse();
    }

    @Test
    @DisplayName("존재하는 슬러그 조회 시 true를 반환한다")
    void existsBySlug_returnsTrue_whenExists() {
        assertThat(
            bootcampRepository.existsBySlugValue(savedBootcamp.getSlug().getValue())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 슬러그 조회 시 false를 반환한다")
    void existsBySlug_returnsFalse_whenNotExists() {
        assertThat(bootcampRepository.existsBySlugValue(NON_EXISTING_SLUG_VALUE)).isFalse();
    }

    @Test
    @DisplayName("다른 ID에서 동일 이름이 있으면 existsByNameAndIdNot이 true를 반환한다")
    void existsByNameAndIdNot_returnsTrue_whenDifferentId() {
        assertThat(bootcampRepository.existsByNameAndIdNot(savedBootcamp.getName(),
            NON_EXISTING_BOOTCAMP_ID)).isTrue();
    }

    @Test
    @DisplayName("동일 ID를 제외하면 existsByNameAndIdNot이 false를 반환한다")
    void existsByNameAndIdNot_returnsFalse_whenSameId() {
        assertThat(
            bootcampRepository.existsByNameAndIdNot(savedBootcamp.getName(),
                savedBootcamp.getId())).isFalse();
    }

    @Test
    @DisplayName("다른 ID에서 동일 슬러그가 있으면 existsBySlugAndIdNot이 true를 반환한다")
    void existsBySlugAndIdNot_returnsTrue_whenDifferentId() {
        assertThat(bootcampRepository.existsBySlugValueAndIdNot(savedBootcamp.getSlug().getValue(),
            NON_EXISTING_BOOTCAMP_ID)).isTrue();
    }

    @Test
    @DisplayName("동일 ID를 제외하면 existsBySlugAndIdNot이 false를 반환한다")
    void existsBySlugAndIdNot_returnsFalse_whenSameId() {
        assertThat(bootcampRepository.existsBySlugValueAndIdNot(savedBootcamp.getSlug().getValue(),
            savedBootcamp.getId())).isFalse();
    }

    @Test
    @DisplayName("트랙이 있는 부트캠프 조회 시 트랙을 함께 반환한다")
    void findWithTracksById_returnsBootcampWithTracks() {
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();
        savedBootcamp.addTrack(track);

        var result = bootcampRepository.findWithTracksById(savedBootcamp.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTracks()).hasSize(1);
        assertThat(result.get().getTracks().getFirst().getTrackType()).isEqualTo(
            track.getTrackType());
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 빈 Optional을 반환한다")
    void findWithTracksById_returnsEmpty_whenNotFound() {
        Optional<Bootcamp> result = bootcampRepository.findWithTracksById(NON_EXISTING_BOOTCAMP_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("slug로 트랙이 없는 부트캠프 조회 시 빈 트랙 목록을 반환한다")
    void findWithTracksBySlug_returnsBootcampWithEmptyTracks_whenNoTracks() {
        var result = bootcampRepository.findWithTracksBySlug(savedBootcamp.getSlug().getValue());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(savedBootcamp.getName());
        assertThat(result.get().getTracks()).isEmpty();
    }

    @Test
    @DisplayName("slug로 트랙이 있는 부트캠프 조회 시 트랙을 함께 반환한다")
    void findWithTracksBySlug_returnsBootcampWithTracks() {
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();
        savedBootcamp.addTrack(track);

        var result = bootcampRepository.findWithTracksBySlug(savedBootcamp.getSlug().getValue());

        assertThat(result).isPresent();
        assertThat(result.get().getTracks()).hasSize(1);
        assertThat(result.get().getTracks().getFirst().getTrackType()).isEqualTo(
            track.getTrackType());
    }

    @Test
    @DisplayName("존재하지 않는 slug 조회 시 빈 Optional을 반환한다")
    void findWithTracksBySlug_returnsEmpty_whenNotFound() {
        var result = bootcampRepository.findWithTracksBySlug(NON_EXISTING_SLUG_VALUE);

        assertThat(result).isEmpty();
    }
}
