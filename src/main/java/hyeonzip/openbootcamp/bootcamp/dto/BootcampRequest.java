package hyeonzip.openbootcamp.bootcamp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

public record BootcampRequest(
        @NotBlank(message = "부트캠프 이름은 필수입니다.")
        @Length(max = 30, message = "부트캠프 이름은 30자 이하여야 합니다.")
        String name,

        @NotBlank(message = "부트캠프 영문명은 필수입니다.")
        @Length(max = 50, message = "부트캠프 영문명은 50자 이하여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9 -]+$", message = "영문명은 영문자, 숫자, 공백, 하이픈만 입력 가능합니다.")
        String englishName,

        @URL
        String logoUrl,

        @Size(max = 500, message = "소개는 500자 이하여야 합니다.")
        String description,

        @URL
        @NotBlank(message = "공식 사이트 URL은 필수입니다.")
        String officialUrl,

        @Valid
        List<BootcampTrackRequest> tracks
) {}
