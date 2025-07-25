package Oops.backend.domain.auth.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.domain.auth.service.AuthService;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록")
    @PostMapping("/join")
    public ResponseEntity<BaseResponse> join(@Valid @RequestBody JoinDto joinDto, HttpServletResponse response) {
        this.authService.join(joinDto, response);
        return BaseResponse.onSuccess(SuccessStatus._CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginDto loginDto, HttpServletResponse request) {
        authService.login(loginDto, request);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<BaseResponse> getUserInfo(@AuthenticatedUser User user) {
        String name = user.getUserName();
        return BaseResponse.onSuccess(SuccessStatus._OK, name);
    }
}