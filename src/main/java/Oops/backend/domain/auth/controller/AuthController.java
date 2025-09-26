package Oops.backend.domain.auth.controller;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.dto.request.ChangePasswordDto;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.service.AuthService;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginDto dto, HttpServletResponse response) {
        authService.login(dto, response);

        return BaseResponse.onSuccess(SuccessStatus._OK, "로그인 완료");
    }

    @PostMapping("/join")
    public ResponseEntity<BaseResponse> join(@RequestBody JoinDto dto) {
        authService.join(dto);

        return BaseResponse.onSuccess(SuccessStatus._OK, "회원가입 완료");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto, @AuthenticatedUser User user) {
        authService.changePassword(user, dto.getOldPassword(), dto.getNewPassword());
        return BaseResponse.onSuccess(SuccessStatus._OK, "비밀번호가 성공적으로 변경되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse> refreshToken(
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshHeader
    ) {
        String refreshToken = extractRefreshToken(request, refreshHeader);
        TokenResponseDto dto = authService.refreshAccessToken(refreshToken);
        authService.setCookie(response, dto.getAccessToken());
        authService.setCookieForRefreshToken(response, dto.getRefreshToken());
        return BaseResponse.onSuccess(SuccessStatus._OK, dto);
    }

    // private method
    private String extractRefreshToken(HttpServletRequest request, String header) {
        if (header != null && !header.isBlank()) {
            String v = header.trim();
            if (v.regionMatches(true, 0, "Bearer ", 0, 7)) v = v.substring(7).trim();
            if (!v.isEmpty()) return v;
        }
        Cookie[] cookies = request.getCookies();
        log.info("AuthController cookies: {}", Arrays.toString(cookies));
        if (cookies == null) throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN, "쿠키 없음");

        String raw = Arrays.stream(cookies)
                .filter(c -> "RefreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN, "RefreshToken 없음"));

        String v = raw.trim();
        if (v.startsWith("\"") && v.endsWith("\"")) v = v.substring(1, v.length() - 1);
        if (v.regionMatches(true, 0, "Bearer ", 0, 7)) v = v.substring(7).trim();
        if (v.isEmpty()) throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN, "RefreshToken 비어있음");
        return v;
    }
}
