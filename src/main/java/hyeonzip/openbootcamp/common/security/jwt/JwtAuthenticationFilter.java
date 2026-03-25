package hyeonzip.openbootcamp.common.security.jwt;

import hyeonzip.openbootcamp.common.security.cookie.CookieProvider;
import hyeonzip.openbootcamp.user.domain.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        cookieProvider.extractAccessToken(request)
            .filter(jwtProvider::isTokenValid)
            .ifPresent(token -> {
                Long userId = jwtProvider.getUserId(token);
                String role = jwtProvider.getRole(token);
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                        userId, null,
                        List.of(new SimpleGrantedAuthority(Role.valueOf(role).getAuthority()))
                    )
                );
            });

        filterChain.doFilter(request, response);
    }
}
