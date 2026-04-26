package hyeonzip.openbootcamp.user.controller;

import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import hyeonzip.openbootcamp.common.response.ApiResponse;
import hyeonzip.openbootcamp.common.security.cookie.CookieProvider;
import hyeonzip.openbootcamp.common.security.jwt.JwtProvider;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.dto.UserResponse;
import hyeonzip.openbootcamp.user.service.ports.inp.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(
        HttpServletRequest request, HttpServletResponse response) {

        String rtValue = cookieProvider.extractRefreshToken(request)
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.INVALID_REFRESH_TOKEN));

        User user = authService.refresh(rtValue);
        String newAt = jwtProvider.issue(user.getId(), user.getRole().name()).accessToken();
        cookieProvider.addAccessTokenCookie(response, newAt);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
        HttpServletRequest request, HttpServletResponse response) {

        String rtValue = cookieProvider.extractRefreshToken(request)
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.INVALID_REFRESH_TOKEN));

        authService.logout(rtValue);
        cookieProvider.clearTokenCookies(response);

        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(
        @AuthenticationPrincipal Long userId) {

        User user = authService.findById(userId);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(user)));
    }
}