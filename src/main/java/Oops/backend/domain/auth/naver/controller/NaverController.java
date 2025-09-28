package Oops.backend.domain.auth.naver.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.dto.request.NaverLoginRequestDto;
import Oops.backend.domain.auth.naver.service.NaverService;
import Oops.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/naver")
public class NaverController {

    private final NaverService naverService;

    @GetMapping("/callback")
    public ResponseEntity<BaseResponse> callback(@RequestParam("code") String code,
                                                 @RequestParam(value = "state", required = false) String state,
                                                 @RequestParam(value = "redirect-url", required = false) String redirectUrl,
                                                 HttpServletResponse response) {
        naverService.loginAndSetCookie(code, state, redirectUrl, response);
        return BaseResponse.onSuccess(SuccessStatus._OK, "네이버 인증 완료");
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody NaverLoginRequestDto dto) {
        naverService.login(dto);
        return BaseResponse.onSuccess(SuccessStatus._OK, "네이버 로그인 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(User user,
                                               @RequestParam(value = "redirect-url", required = false) String redirectUrl, HttpServletResponse response) {
        naverService.logout(user.getId(), redirectUrl, response);
        return BaseResponse.onSuccess(SuccessStatus._OK, "네이버 로그아웃");
    }
}