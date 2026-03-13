package hyeonzip.openbootcamp.bootcamp.repository;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TrackType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BootcampRepositoryTest {

    @Autowired
    private BootcampRepository bootcampRepository;

    @Autowired
    private TestEntityManager em;

    private Bootcamp persistedBootcamp;

    @BeforeEach
    void setUp() {
        persistedBootcamp = em.persistAndFlush(
                Bootcamp.builder()
                        .name("위코드")
                        .slug("wecode")
                        .build()
        );
    }

    @Test
    @DisplayName("존재하는 이름 조회 시 true를 반환한다")
    void existsByName_returnsTrue_whenExists() {
        assertThat(bootcampRepository.existsByName("위코드")).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이름 조회 시 false를 반환한다")
    void existsByName_returnsFalse_whenNotExists() {
        assertThat(bootcampRepository.existsByName("코드스테이츠")).isFalse();
    }

    @Test
    @DisplayName("존재하는 슬러그 조회 시 true를 반환한다")
    void existsBySlug_returnsTrue_whenExists() {
        assertThat(bootcampRepository.existsBySlug("wecode")).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 슬러그 조회 시 false를 반환한다")
    void existsBySlug_returnsFalse_whenNotExists() {
        assertThat(bootcampRepository.existsBySlug("codestates")).isFalse();
    }

    @Test
    @DisplayName("다른 ID에서 동일 이름이 있으면 existsByNameAndIdNot이 true를 반환한다")
    void existsByNameAndIdNot_returnsTrue_whenDifferentId() {
        assertThat(bootcampRepository.existsByNameAndIdNot("위코드", 999L)).isTrue();
    }

    @Test
    @DisplayName("동일 ID를 제외하면 existsByNameAndIdNot이 false를 반환한다")
    void existsByNameAndIdNot_returnsFalse_whenSameId() {
        assertThat(bootcampRepository.existsByNameAndIdNot("위코드", persistedBootcamp.getId())).isFalse();
    }

    @Test
    @DisplayName("다른 ID에서 동일 슬러그가 있으면 existsBySlugAndIdNot이 true를 반환한다")
    void existsBySlugAndIdNot_returnsTrue_whenDifferentId() {
        assertThat(bootcampRepository.existsBySlugAndIdNot("wecode", 999L)).isTrue();
    }

    @Test
    @DisplayName("동일 ID를 제외하면 existsBySlugAndIdNot이 false를 반환한다")
    void existsBySlugAndIdNot_returnsFalse_whenSameId() {
        assertThat(bootcampRepository.existsBySlugAndIdNot("wecode", persistedBootcamp.getId())).isFalse();
    }

    @Test
    @DisplayName("트랙이 있는 부트캠프 조회 시 트랙을 함께 반환한다")
    void findWithTracksById_returnsBootcampWithTracks() {
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .operationType(OperationType.ONLINE)
                .build();
        persistedBootcamp.addTrack(track);
        em.flush();
        em.clear();

        Optional<Bootcamp> result = bootcampRepository.findWithTracksById(persistedBootcamp.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTracks()).hasSize(1);
        assertThat(result.get().getTracks().get(0).getTrackType()).isEqualTo(TrackType.BACKEND);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 빈 Optional을 반환한다")
    void findWithTracksById_returnsEmpty_whenNotFound() {
        Optional<Bootcamp> result = bootcampRepository.findWithTracksById(999L);

        assertThat(result).isEmpty();
    }
}
