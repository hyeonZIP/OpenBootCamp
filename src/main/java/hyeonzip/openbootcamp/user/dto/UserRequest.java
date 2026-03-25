package hyeonzip.openbootcamp.user.dto;

import hyeonzip.openbootcamp.user.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

public record UserRequest(
    @NotBlank(message = "사용자명은 필수입니다.")
    @Length(max = 50, message = "사용자명은 50자 이하여야 합니다.")
    String username,

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @URL
    String avatarUrl,

    @NotNull(message = "역할은 필수입니다.")
    Role role
) {

}
