package Oops.backend.domain.auth.kakao.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.dto.request.KakaoLoginRequestDto;
import Oops.backend.domain.auth.kakao.service.KakaoService;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/callback")
    public ResponseEntity<BaseResponse> callback(@RequestParam String code,
                                                 @RequestParam(value="redirect-url", required=false) String redirectUrl, HttpServletResponse response) {
        kakaoService.loginAndSetCookie(code, redirectUrl, response);

        return BaseResponse.onSuccess(SuccessStatus._OK, "카카오 인증 완료");
    }


    @PostMapping("/login")
    public ResponseEntity<BaseResponse> kakaoCallback(@RequestBody KakaoLoginRequestDto kakaoLoginRequestDto) {
        this.kakaoService.login(kakaoLoginRequestDto);
        return BaseResponse.onSuccess(SuccessStatus._OK, "카카오 로그인 성공");
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


//    @GetMapping("/test")
//    public ResponseEntity<BaseResponse> testKakaoLogin(
//            @RequestParam(value = "code", required = false) String code, @AuthenticatedUser User user
//    ) {
//        log.info("카카오 로그인 테스트 호출됨, code={}", code + user.getUserName());
//        log.info(user.getUserName());
//
//        if (code == null || code.isBlank()) {
//            return BaseResponse.onSuccess(SuccessStatus._OK, "테스트 OK - code 없음");
//        }
//
//        var result = kakaoService.login(code, null);
//        return BaseResponse.onSuccess(SuccessStatus._OK, result);
//    }
}
