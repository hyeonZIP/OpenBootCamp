package hyeonzip.openbootcamp.bootcamp.repository;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TrackType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BootcampTrackRepositoryTest {

    @Autowired
    private BootcampTrackRepository bootcampTrackRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("부트캠프 ID와 트랙 ID가 일치하면 트랙을 반환한다")
    void findByIdAndBootcampId_returnsTrack_whenBothMatch() {
        Bootcamp bootcamp = em.persist(
                Bootcamp.builder().name("위코드").slug(Slug.from("wecode")).build()
        );
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .operationType(OperationType.ONLINE)
                .build();
        bootcamp.addTrack(track);
        em.flush();

        Optional<BootcampTrack> result = bootcampTrackRepository.findByIdAndBootcampId(
                track.getId(), bootcamp.getId()
        );

        assertThat(result).isPresent();
        assertThat(result.get().getTrackType()).isEqualTo(TrackType.BACKEND);
    }

    @Test
    @DisplayName("부트캠프 ID가 다르면 빈 Optional을 반환한다")
    void findByIdAndBootcampId_returnsEmpty_whenBootcampIdMismatch() {
        Bootcamp bootcamp = em.persist(
                Bootcamp.builder().name("위코드").slug(Slug.from("wecode")).build()
        );
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .operationType(OperationType.ONLINE)
                .build();
        bootcamp.addTrack(track);
        em.flush();

        Optional<BootcampTrack> result = bootcampTrackRepository.findByIdAndBootcampId(
                track.getId(), 999L
        );

        assertThat(result).isEmpty();
    }
}
