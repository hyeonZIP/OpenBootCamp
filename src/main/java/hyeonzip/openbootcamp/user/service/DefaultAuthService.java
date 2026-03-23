package hyeonzip.openbootcamp.user.service;

import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import hyeonzip.openbootcamp.common.security.oauth2.userinfo.OAuth2UserInfo;
import hyeonzip.openbootcamp.user.domain.Role;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.repository.UserRepository;
import hyeonzip.openbootcamp.user.service.ports.inp.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User upsertFromOAuth2(OAuth2UserInfo userInfo) {
        return userRepository.findByGithubId(userInfo.getId())
            .map(user -> {
                user.updateProfile(userInfo.getUsername(), userInfo.getEmail(),
                    userInfo.getAvatarUrl());
                return user;
            })
            .orElseGet(() -> userRepository.save(
                User.create(userInfo.getId(), userInfo.getUsername(), userInfo.getEmail(),
                    userInfo.getAvatarUrl(), Role.STUDENT)
            ));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.USER_NOT_FOUND));
    }
}
