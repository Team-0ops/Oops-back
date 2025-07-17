package Oops.backend.domain.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "ThisIsASecretKeyWithAtLeast32Characters!!@#"; // 최소 32자 이상
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());
    private final long expiration = 1000 * 60 * 60 * 24; // 24시간

    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        return Long.parseLong(
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                        .getBody().getSubject()
        );
    }
}
