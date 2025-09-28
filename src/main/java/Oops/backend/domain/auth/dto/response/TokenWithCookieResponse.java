package Oops.backend.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
@AllArgsConstructor(staticName = "of")
public class TokenWithCookieResponse {
    private final String accessToken;
    private final String refreshToken;
    private final ResponseCookie accessCookie;
    private final ResponseCookie refreshCookie;
}

