package hyeonzip.openbootcamp.bootcamp.dto;

import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BootcampTrackRequest(
        @NotNull(message = "트랙 유형은 필수입니다.")
        TrackType trackType,

        @NotNull(message = "운영 형태는 필수입니다.")
        OperationType operationType,

        List<TechStack> techStacks,

        Integer priceMin,
        Integer priceMax,
        Integer durationWeeks,
        Boolean isRecruiting
) {}
