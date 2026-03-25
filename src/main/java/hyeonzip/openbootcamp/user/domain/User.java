package hyeonzip.openbootcamp.user.domain;

import hyeonzip.openbootcamp.common.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String username;

    private String email;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    private User(String username, String email, String avatarUrl, Role role) {
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.role = role;
    }

    public static User create(String username, String email, String avatarUrl, Role role) {
        return User.builder()
            .username(username)
            .email(email)
            .avatarUrl(avatarUrl)
            .role(role)
            .build();
    }

    public void updateProfile(String username, String email, String avatarUrl) {
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public void changeRole(Role role) {
        this.role = role;
    }
}
