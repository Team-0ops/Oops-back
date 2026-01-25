package Oops.backend.common.security.token;


import Oops.backend.common.security.util.JwtTokenProvider;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.entity.RefreshToken;
import Oops.backend.domain.auth.repository.RefreshTokenRepository;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenCookieProps tokenCookieProps;

    public TokenResponseDto issue(User user) {
        String access = jwtTokenProvider.generateAccessToken(user.getId());
        String refresh = jwtTokenProvider.generateRefreshToken(user.getId());

        RefreshToken rt = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> RefreshToken.of(user.getId(), refresh));
        rt.setToken(refresh);
        refreshTokenRepository.save(rt);

        return TokenResponseDto.of(access, refresh);
    }

    public void setLoginCookies(HttpServletResponse res, TokenResponseDto tokens) {
        ResponseCookie access = ResponseCookie.from(tokenCookieProps.getAccessCookieName(), tokens.getAccessToken())
                .httpOnly(true)
                .secure(tokenCookieProps.isSecure())
                .sameSite(tokenCookieProps.getSameSite())
                .path(tokenCookieProps.getPath())
                .maxAge(tokenCookieProps.getAccessMaxAgeSec())
                .build();

        ResponseCookie refresh = ResponseCookie.from(tokenCookieProps.getRefreshCookieName(), tokens.getRefreshToken())
                .httpOnly(true)
                .secure(tokenCookieProps.isSecure())
                .sameSite(tokenCookieProps.getSameSite())
                .path(tokenCookieProps.getPath())
                .maxAge(tokenCookieProps.getRefreshMaxAgeSec())
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        res.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }

    public void revokeAllAndClearCookies(Long userId, HttpServletResponse res) {
        refreshTokenRepository.deleteByUserId(userId);
        // 만료시켜서 브라우저 쿠키 제거
        ResponseCookie expiredAccess = ResponseCookie.from(tokenCookieProps.getAccessCookieName(), "")
                .httpOnly(true).secure(tokenCookieProps.isSecure())
                .sameSite(tokenCookieProps.getSameSite())
                .path(tokenCookieProps.getPath())
                .maxAge(0)
                .build();

        ResponseCookie expiredRefresh = ResponseCookie.from(tokenCookieProps.getRefreshCookieName(), "")
                .httpOnly(true).secure(tokenCookieProps.isSecure())
                .sameSite(tokenCookieProps.getSameSite())
                .path(tokenCookieProps.getPath())
                .maxAge(0)
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, expiredAccess.toString());
        res.addHeader(HttpHeaders.SET_COOKIE, expiredRefresh.toString());
    }
}
