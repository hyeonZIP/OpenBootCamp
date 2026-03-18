package hyeonzip.openbootcamp.bootcamp.dto;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                Optional.ofNullable(bootcamp.getSlug()).map(Slug::getValue).orElse(null),
                bootcamp.getLogoUrl(),
                bootcamp.getDescription(),
                bootcamp.getOfficialUrl(),
                Optional.ofNullable(bootcamp.getTracks())
                    .orElseGet(List::of)
                    .stream()
                    .map(BootcampTrackResponse::from)
                    .toList(),
                bootcamp.getCreatedAt(),
                bootcamp.getUpdatedAt()
        );
    }
}
