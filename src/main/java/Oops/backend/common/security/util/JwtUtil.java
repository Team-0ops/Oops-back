package Oops.backend.common.security.util;


import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtTokenProvider jwtProvider;

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProvider.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | io.jsonwebtoken.security.SignatureException e) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED, "토큰 만료");
        } catch (UnsupportedJwtException e) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        } catch (Exception e) {
            log.warn("Unexpected JWT error: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public Long extractUserIdFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._NOT_FOUND);
        }
    }

}
