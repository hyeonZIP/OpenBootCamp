package hyeonzip.openbootcamp.user.domain;

import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum OAuthProvider {

    GITHUB("github"),
    KAKAO("kakao"),
    GOOGLE("google");

    private final String registrationId;

    OAuthProvider(String registrationId) {
        this.registrationId = registrationId;
    }

    public static OAuthProvider from(String registrationId) {
        return Arrays.stream(values())
            .filter(p -> p.registrationId.equalsIgnoreCase(registrationId))
            .findFirst()
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.UNSUPPORTED_OAUTH2_PROVIDER));
    }
}
