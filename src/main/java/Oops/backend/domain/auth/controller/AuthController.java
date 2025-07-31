package Oops.backend.domain.auth.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.domain.auth.service.AuthService;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "로그인 및 회원가입 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록")
    @PostMapping("/join")
    public ResponseEntity<BaseResponse> join(@Valid @RequestBody JoinDto joinDto) {
        this.authService.join(joinDto);
        return BaseResponse.onSuccess(SuccessStatus._CREATED);
    }

    // 로그인
    @Operation(summary = "로그인", description = "기존 사용자 로그인")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginDto loginDto, HttpServletResponse request) {
        String accessToken = authService.login(loginDto, request);
        return BaseResponse.onSuccess(SuccessStatus._OK, accessToken);
    }
    @Operation(summary = "사용자 정보 조회")
    @GetMapping("/getUserInfo")
    public ResponseEntity<BaseResponse> getUserInfo(@AuthenticatedUser User user) {
        String name = user.getUserName();
        return BaseResponse.onSuccess(SuccessStatus._OK, name);
    }
}