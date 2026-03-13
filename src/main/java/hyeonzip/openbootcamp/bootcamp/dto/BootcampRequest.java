package hyeonzip.openbootcamp.bootcamp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BootcampRequest(
        @NotBlank(message = "부트캠프 이름은 필수입니다.")
        String name,

        String logoUrl,

        @Size(max = 500, message = "소개는 500자 이하여야 합니다.")
        String description,

        String officialUrl,

        @Valid
        List<BootcampTrackRequest> tracks
) {}
