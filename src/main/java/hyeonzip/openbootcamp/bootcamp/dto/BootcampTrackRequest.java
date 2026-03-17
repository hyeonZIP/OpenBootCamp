package hyeonzip.openbootcamp.bootcamp.dto;

import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BootcampTrackRequest(
        @NotNull(message = "트랙 유형은 필수입니다.")
        TrackType trackType,

        @NotNull(message = "운영 형태는 필수입니다.")
        OperationType operationType,

        List<TechStack> techStacks,

        // 선택 필드 — null 허용, 값이 있을 때만 범위 검증
        @Min(value = 0, message = "수강료 최솟값은 0 이상이어야 합니다.")
        Integer priceMin,

        @Min(value = 0, message = "수강료 최댓값은 0 이상이어야 합니다.")
        Integer priceMax,

        @Min(value = 1, message = "교육 기간은 최소 1주 이상이어야 합니다.")
        @Max(value = 104, message = "교육 기간은 최대 104주(2년) 이하여야 합니다.")
        Integer durationWeeks,

        Boolean isRecruiting
) {}
