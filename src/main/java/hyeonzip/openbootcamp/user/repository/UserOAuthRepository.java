package hyeonzip.openbootcamp.user.repository;

import hyeonzip.openbootcamp.user.domain.OAuthProvider;
import hyeonzip.openbootcamp.user.domain.UserOAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOAuthRepository extends JpaRepository<UserOAuth, Long> {

    Optional<UserOAuth> findByProviderAndProviderId(OAuthProvider provider, String providerId);
}
