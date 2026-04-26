package hyeonzip.openbootcamp.user.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hyeonzip.openbootcamp.user.domain.RefreshToken;
import hyeonzip.openbootcamp.user.domain.User;
import hyeonzip.openbootcamp.user.fixture.RefreshTokenFixture;
import hyeonzip.openbootcamp.user.fixture.UserFixture;
import hyeonzip.openbootcamp.user.fixture.UserRequestFixture;
import hyeonzip.openbootcamp.user.repository.RefreshTokenRepository;
import hyeonzip.openbootcamp.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EntityManager entityManager;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(UserFixture.user());
        refreshTokenRepository.save(RefreshTokenFixture.refreshToken(savedUser));
        entityManager.flush();
        entityManager.clear();
    }

    // ── POST /auth/refresh ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /auth/refresh - 유효한 RT 쿠키 → 200")
    void refresh_validToken_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                .cookie(new Cookie("refreshToken", RefreshTokenFixture.TOKEN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /auth/refresh - RT 쿠키 없음 → 401")
    void refresh_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/refresh - 무효화된 RT → 401")
    void refresh_invalidatedToken_returns401() throws Exception {
        RefreshToken token = refreshTokenRepository.findByToken(RefreshTokenFixture.TOKEN).get();
        token.invalidate();
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(post("/api/v1/auth/refresh")
                .cookie(new Cookie("refreshToken", RefreshTokenFixture.TOKEN)))
            .andExpect(status().isUnauthorized());
    }

    // ── POST /auth/logout ────────────────────────────────────────────────

    @Test
    @DisplayName("POST /auth/logout - 유효한 RT 쿠키 → 200")
    void logout_validToken_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                .with(authentication(new UsernamePasswordAuthenticationToken(
                    savedUser.getId(), null, List.of())))
                .cookie(new Cookie("refreshToken", RefreshTokenFixture.TOKEN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /auth/logout - RT 쿠키 없음 → 401")
    void logout_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                .with(authentication(new UsernamePasswordAuthenticationToken(
                    savedUser.getId(), null, List.of()))))
            .andExpect(status().isUnauthorized());
    }

    // ── GET /auth/me ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /auth/me - 인증된 사용자 → 200 + 사용자 정보")
    void me_authenticated_returns200WithUserInfo() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                .with(authentication(new UsernamePasswordAuthenticationToken(
                    savedUser.getId(), null, List.of()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(savedUser.getId()))
            .andExpect(jsonPath("$.data.username").value(UserRequestFixture.USERNAME))
            .andExpect(jsonPath("$.data.email").value(UserRequestFixture.EMAIL));
    }
}