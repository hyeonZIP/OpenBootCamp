package hyeonzip.openbootcamp.bootcamp.service.ports.inp;

import hyeonzip.openbootcamp.bootcamp.dto.BootcampRequest;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampResponse;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampTrackRequest;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampTrackResponse;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BootcampService {

    Page<BootcampResponse> getBootcamps(
            TrackType trackType, OperationType operationType,
            TechStack techStack, String keyword, Pageable pageable);

    BootcampResponse getBootcamp(Long id);

    BootcampResponse createBootcamp(BootcampRequest request);

    BootcampResponse updateBootcamp(Long id, BootcampRequest request);

    void deleteBootcamp(Long id);

    BootcampTrackResponse addTrack(Long bootcampId, BootcampTrackRequest request);

    BootcampTrackResponse updateTrack(Long bootcampId, Long trackId, BootcampTrackRequest request);

    void deleteTrack(Long bootcampId, Long trackId);
}
