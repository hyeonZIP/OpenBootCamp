package hyeonzip.openbootcamp.common.security.jwt;

import hyeonzip.openbootcamp.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;

    public TokenPair issue(User user) {
        return new TokenPair(
            jwtProvider.generateAccessToken(user.getId(), user.getRole().name()),
            jwtProvider.generateRefreshToken(user.getId())
        );
    }
}
