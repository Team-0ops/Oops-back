package Oops.backend.domain.auth.kakao.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.dto.request.KakaoLoginRequestDto;
import Oops.backend.domain.auth.dto.response.LoginResponse;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.dto.response.TokenWithCookieResponse;
import Oops.backend.domain.auth.kakao.service.KakaoService;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/test")
    public ResponseEntity<BaseResponse> testKakaoLogin(
            @RequestParam(value = "code", required = false) String code, @AuthenticatedUser User user
    ) {
        log.info("카카오 로그인 테스트 호출됨, code={}", code + user.getUserName());
        log.info(user.getUserName());

        if (code == null || code.isBlank()) {
            return BaseResponse.onSuccess(SuccessStatus._OK, "테스트 OK - code 없음");
        }

        var result = kakaoService.login(code, null);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    @GetMapping("/callback")
    public ResponseEntity<BaseResponse> callback(@RequestParam String code,
                                                 @RequestParam(value="redirect-url", required=false) String redirectUrl, HttpServletResponse response) {
        kakaoService.loginAndSetCookie(code, redirectUrl, response);

        return BaseResponse.onSuccess(SuccessStatus._OK, "카카오 인증 완료");
    }


    @GetMapping("/kakao/login")
    public ResponseEntity<BaseResponse> kakaoCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "redirect-url", required = false) String redirectUrl
    ) {
        this.kakaoService.login(code, redirectUrl);
        return BaseResponse.onSuccess(SuccessStatus._OK, "카카오 로그인 성공");
    }

    @PostMapping("/kakao/login")
    public ResponseEntity<BaseResponse> kakaoCallback(@RequestBody KakaoLoginRequestDto kakaoLoginRequestDto) {
        this.kakaoService.login(kakaoLoginRequestDto);
        return BaseResponse.onSuccess(SuccessStatus._OK, "카카오 로그인 성공");
    }


    @PostMapping("/kakao/logout")
    public ResponseEntity<BaseResponse> logout(
             User user,
            @RequestParam(value = "redirect-url", required = false) String redirectUrl){
        this.kakaoService.logout(user.getId(), redirectUrl);
        return BaseResponse.onSuccess(SuccessStatus._OK, "카카오 로그아웃");
    }

}
