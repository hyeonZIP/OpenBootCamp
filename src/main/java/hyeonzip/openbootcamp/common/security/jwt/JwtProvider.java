package hyeonzip.openbootcamp.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "ACCESS";
    private static final String TYPE_REFRESH = "REFRESH";

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-token-expiry}")
    private long accessTokenExpiry;

    @Value("${app.jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenPair issue(Long userId, String role) {
        return new TokenPair(
            generateAccessToken(userId, role),
            generateRefreshToken(userId)
        );
    }

    private String generateAccessToken(Long userId, String role) {
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim(CLAIM_TYPE, TYPE_ACCESS)
            .claim(CLAIM_ROLE, role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
            .signWith(getSigningKey())
            .compact();
    }

    private String generateRefreshToken(Long userId) {
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim(CLAIM_TYPE, TYPE_REFRESH)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiry))
            .signWith(getSigningKey())
            .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public Optional<Claims> parseClaimsOrEmpty(String token) {
        try {
            return Optional.of(parseClaims(token));
        } catch (ExpiredJwtException e) {
            log.debug("JWT 토큰 만료: {}", e.getMessage());
            return Optional.empty();
        } catch (SignatureException e) {
            log.warn("JWT 서명 불일치 (위변조 의심): {}", e.getMessage());
            return Optional.empty();
        } catch (MalformedJwtException e) {
            log.warn("JWT 형식 오류: {}", e.getMessage());
            return Optional.empty();
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 형식: {}", e.getMessage());
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰 값이 null 또는 빈 문자열: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean isAccessToken(Claims claims) {
        return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public Long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    public String getRole(Claims claims) {
        return claims.get(CLAIM_ROLE, String.class);
    }
}
