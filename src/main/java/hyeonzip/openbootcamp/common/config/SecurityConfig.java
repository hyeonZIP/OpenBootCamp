package hyeonzip.openbootcamp.common.config;

import hyeonzip.openbootcamp.common.security.jwt.JwtAuthenticationFilter;
import hyeonzip.openbootcamp.common.security.oauth2.CustomOAuth2UserService;
import hyeonzip.openbootcamp.common.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import hyeonzip.openbootcamp.user.domain.Role;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Profile("!test")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${spring.h2.console.enabled}")
    private boolean h2ConsoleEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .headers(headers -> {
                if (h2ConsoleEnabled) {
                    headers.frameOptions(FrameOptionsConfig::sameOrigin);
                }
            })
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── [PUBLIC] 비로그인 허용 ─────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/github/callback").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/bootcamps/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/projects/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/pull-requests/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()
                // H2 콘솔 (개발 편의)
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/favicon.ico/**").permitAll()

                // ── [ADMIN] 플랫폼 관리자 전용 ────────────────────────────────
                .requestMatchers(HttpMethod.DELETE, "/api/v1/bootcamps/**")
                .hasAuthority(Role.ADMIN.getAuthority())
                .requestMatchers(HttpMethod.GET, "/api/v1/admin/**")
                .hasAuthority(Role.ADMIN.getAuthority())
                .requestMatchers(HttpMethod.PUT, "/api/v1/admin/**")
                .hasAuthority(Role.ADMIN.getAuthority())
                .requestMatchers(HttpMethod.GET, "/api/v1/github/rate-limit")
                .hasAuthority(Role.ADMIN.getAuthority())

                // ── [BOOTCAMP_ADMIN] 운영사 이상 ──────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/v1/bootcamps/**")
                .hasAnyAuthority(Role.BOOTCAMP_ADMIN.getAuthority(), Role.ADMIN.getAuthority())
                .requestMatchers(HttpMethod.PUT, "/api/v1/bootcamps/**")
                .hasAnyAuthority(Role.BOOTCAMP_ADMIN.getAuthority(), Role.ADMIN.getAuthority())
                .requestMatchers(HttpMethod.GET, "/api/v1/admin/bootcamp/dashboard")
                .hasAnyAuthority(Role.BOOTCAMP_ADMIN.getAuthority(), Role.ADMIN.getAuthority())

                // ── [AUTH] 로그인 필요 ─────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/projects/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/projects/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/me/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/me/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/github/sync/**").authenticated()

                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                    handlerExceptionResolver.resolveException(req, res, null, e))
                .accessDeniedHandler((req, res, e) ->
                    handlerExceptionResolver.resolveException(req, res, null, e))
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
