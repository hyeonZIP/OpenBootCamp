package hyeonzip.openbootcamp.user.domain;

import java.util.Optional;
import org.springframework.util.StringUtils;

public enum Role {
    STUDENT, BOOTCAMP_ADMIN, ADMIN;

    private static final String PREFIX = "ROLE_";

    public String getAuthority() {
        return PREFIX + this.name();
    }

    public static Optional<Role> from(String value) {
        if (!StringUtils.hasText(value)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Role.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
