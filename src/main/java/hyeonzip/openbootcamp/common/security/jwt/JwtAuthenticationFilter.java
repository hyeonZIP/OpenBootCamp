package hyeonzip.openbootcamp.common.security.jwt;

import hyeonzip.openbootcamp.common.security.cookie.CookieProvider;
import hyeonzip.openbootcamp.user.domain.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        Optional<String> accessTokenCookie = cookieProvider.extractAccessToken(request);

        if (accessTokenCookie.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<Claims> accessTokenClaims = getAccessTokenClaims(accessTokenCookie.get());

        if (accessTokenClaims.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = accessTokenClaims.get();

        Long userId = jwtProvider.getUserId(claims);
        List<SimpleGrantedAuthority> authorities = resolveAuthorities(jwtProvider.getRole(claims));

        setSecurityContextHolder(userId, authorities);

        filterChain.doFilter(request, response);
    }

    private Optional<Claims> getAccessTokenClaims(String accessTokenClaims) {
        Optional<Claims> claims = jwtProvider.parseClaimsSafely(accessTokenClaims);

        if (claims.isPresent() && jwtProvider.isAccessToken(claims.get())) {
            return claims;
        }

        return Optional.empty();
    }

    private void setSecurityContextHolder(Long userId, List<SimpleGrantedAuthority> authorities) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userId, null, authorities)
        );
    }

    private List<SimpleGrantedAuthority> resolveAuthorities(String roleValue) {
        if (!StringUtils.hasText(roleValue)) {
            return List.of();
        }

        try {
            return List.of(new SimpleGrantedAuthority(Role.valueOf(roleValue).getAuthority()));
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
}
