package hyeonzip.openbootcamp.bootcamp.dto;

import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;

import java.time.LocalDateTime;
import java.util.List;

public record BootcampTrackResponse(
        Long id,
        TrackType trackType,
        OperationType operationType,
        List<TechStack> techStacks,
        Integer priceMin,
        Integer priceMax,
        Integer durationWeeks,
        Boolean isRecruiting,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BootcampTrackResponse from(BootcampTrack track) {
        return new BootcampTrackResponse(
                track.getId(),
                track.getTrackType(),
                track.getOperationType(),
                track.getTechStacks(),
                track.getPriceMin(),
                track.getPriceMax(),
                track.getDurationWeeks(),
                track.getIsRecruiting(),
                track.getCreatedAt(),
                track.getUpdatedAt()
        );
    }
}
