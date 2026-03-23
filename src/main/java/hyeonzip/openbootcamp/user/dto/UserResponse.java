package hyeonzip.openbootcamp.user.dto;

import hyeonzip.openbootcamp.user.domain.User;

public record UserResponse(
    Long id,
    String githubId,
    String username,
    String email,
    String avatarUrl,
    String role
) {

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getGithubId(),
            user.getUsername(),
            user.getEmail(),
            user.getAvatarUrl(),
            user.getRole().name()
        );
    }
}
