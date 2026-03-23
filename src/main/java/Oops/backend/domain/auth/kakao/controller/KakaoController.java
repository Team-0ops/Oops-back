package Oops.backend.domain.auth.kakao.controller;


import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.dto.request.KakaoLoginRequestDto;
import Oops.backend.domain.auth.dto.response.LoginResponse;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.kakao.service.KakaoService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    @Operation(
            summary = "카카오 콜백",
            description = "카카오 서버가 호출하는 콜백 엔드포인트입니다. Swagger 수동 테스트보다는 브라우저 OAuth 흐름으로 확인하세요."
    )
    @GetMapping("/callback")
    public void callback(
            @Parameter(description = "카카오가 전달하는 인가 코드", example = "실제_인가코드")
            @RequestParam String code,
            @Parameter(description = "로그인 완료 후 이동할 프론트 주소", example = "http://localhost:5173")
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response
    ) throws IOException {
        kakaoService.loginAndSetCookie(code, state, response);

        String target = (state != null && !state.isBlank())
                ? state
                : "https://www.oops-oopsie.com/";

        response.sendRedirect(target);
    }


    @PostMapping("/login")
    public ResponseEntity<BaseResponse> kakaoCallback(@RequestBody KakaoLoginRequestDto kakaoLoginRequestDto) {
        TokenResponseDto tokenResponseDto = this.kakaoService.login(kakaoLoginRequestDto);
        return BaseResponse.onSuccess(SuccessStatus._OK, tokenResponseDto);
    }


    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(
            @AuthenticatedUser User user,
            @RequestParam(value="redirect-url", required=false) String redirectUrl,
            HttpServletResponse res){
        this.kakaoService.logout(user.getId(), redirectUrl, res);
        return BaseResponse.onSuccess(SuccessStatus._OK, "카카오 로그아웃");
    }




//    @GetMapping("/kakao/login")
//    public ResponseEntity<BaseResponse> kakaoCallback(
//            @RequestParam("code") String code,
//            @RequestParam(value = "redirect-url", required = false) String redirectUrl
//    ) {
//        this.kakaoService.login(code, redirectUrl);
//        return BaseResponse.onSuccess(SuccessStatus._OK, "카카오 로그인 성공");
//    }


    @GetMapping("/test")
    public ResponseEntity<BaseResponse> testKakaoLogin(
            @RequestParam(value = "code", required = false) String code, @AuthenticatedUser User user
    ) {
        log.info("카카오 로그인 테스트 호출됨, code={}", code + user.getUserName());
        log.info(user.getUserName());

        if (code == null || code.isBlank()) {
            return BaseResponse.onSuccess(SuccessStatus._OK, "테스트 OK - code 없음");
        }

        var result = kakaoService.login(code);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
}