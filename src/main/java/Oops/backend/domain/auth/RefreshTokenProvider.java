package Oops.backend.domain.auth;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class RefreshTokenProvider {
    // JwtTokenProvider 키와 다른 키 사용.
    private final SecretKey key; // 시크릿 키
    private final long validityInMilliseconds; // 유효 시간

    public RefreshTokenProvider(@Value("${security.jwt.token.secret-refresh-key}") final String secretKey,
                                @Value("${security.jwt.token.expire-length}") final long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    // RefreshToken 생성
    public String createRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + Duration.ofDays(14).toMillis());

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN, e.getMessage());
        }
    }
}
