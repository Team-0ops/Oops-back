package Oops.backend.domain.auth;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class JwtEncoder {
    private static final String TOKEN_TYPE = "Bearer ";

    public static String decodeJwtBearerToken(final String token) {
        String decodedToken = token.replace("+", " ");

        if (decodedToken.startsWith(TOKEN_TYPE)) {
            return decodedToken.substring(TOKEN_TYPE.length());
        } else if (!decodedToken.isEmpty()) {
            return decodedToken;
        }
        throw new IllegalArgumentException("Invalid JWT token");
    }
    public static String encode(String token) {
        String cookieValue = TOKEN_TYPE + token;
        return URLEncoder.encode(cookieValue, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public static String decode(String cookieValue) {
        String value = URLDecoder.decode(cookieValue, StandardCharsets.UTF_8);
        if (value.startsWith(TOKEN_TYPE)) {
            return value.substring(TOKEN_TYPE.length());
        }
        throw new GeneralException(ErrorStatus.INVALID_TOKEN, "token decode 실패");
    }
}
