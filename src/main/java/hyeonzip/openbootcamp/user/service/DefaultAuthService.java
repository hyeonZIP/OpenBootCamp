package hyeonzip.openbootcamp.user.service;

import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import hyeonzip.openbootcamp.common.security.oauth2.userinfo.OAuth2UserInfo;
import hyeonzip.openbootcamp.user.domain.RefreshToken;
import hyeonzip.openbootcamp.user.domain.Role;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.domain.UserOAuth;
import hyeonzip.openbootcamp.user.repository.RefreshTokenRepository;
import hyeonzip.openbootcamp.user.repository.UserOAuthRepository;
import hyeonzip.openbootcamp.user.repository.UserRepository;
import hyeonzip.openbootcamp.user.service.ports.inp.AuthService;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserOAuthRepository userOAuthRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Override
    @Transactional
    public User upsertFromOAuth2(OAuth2UserInfo userInfo) {
        return userOAuthRepository.findByProviderAndProviderId(userInfo.getProvider(),
                userInfo.getId())
            .map(oauth -> {
                oauth.getUser().updateProfile(userInfo.getUsername(), userInfo.getEmail(),
                    userInfo.getAvatarUrl());
                return oauth.getUser();
            })
            .orElseGet(() -> {
                User user = userRepository.save(
                    User.create(userInfo.getUsername(), userInfo.getEmail(),
                        userInfo.getAvatarUrl(), Role.STUDENT)
                );
                userOAuthRepository.save(
                    UserOAuth.create(user, userInfo.getProvider(), userInfo.getId())
                );
                return user;
            });
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void saveRefreshToken(User user, String tokenValue) {
        LocalDateTime expiresAt = LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiry));
        refreshTokenRepository.save(RefreshToken.create(user, tokenValue, expiresAt));
    }

    @Override
    @Transactional
    public User refresh(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!refreshToken.isValid()) {
            throw new OpenBootCampException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return refreshToken.getUser();
    }

    @Override
    @Transactional
    public void logout(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.INVALID_REFRESH_TOKEN));

        refreshToken.invalidate();
    }
}
