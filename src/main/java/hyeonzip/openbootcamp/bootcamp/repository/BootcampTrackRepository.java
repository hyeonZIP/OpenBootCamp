package hyeonzip.openbootcamp.bootcamp.repository;

import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BootcampTrackRepository extends JpaRepository<BootcampTrack, Long> {

    Optional<BootcampTrack> findByIdAndBootcampId(Long id, Long bootcampId);
}
