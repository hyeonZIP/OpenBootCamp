package hyeonzip.openbootcamp.bootcamp.dto;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;

import java.time.LocalDateTime;
import java.util.List;

public record BootcampResponse(
        Long id,
        String name,
        String slug,
        String logoUrl,
        String description,
        String officialUrl,
        List<BootcampTrackResponse> tracks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BootcampResponse from(Bootcamp bootcamp) {
        return new BootcampResponse(
                bootcamp.getId(),
                bootcamp.getName(),
                bootcamp.getSlug(),
                bootcamp.getLogoUrl(),
                bootcamp.getDescription(),
                bootcamp.getOfficialUrl(),
                bootcamp.getTracks().stream().map(BootcampTrackResponse::from).toList(),
                bootcamp.getCreatedAt(),
                bootcamp.getUpdatedAt()
        );
    }
}
