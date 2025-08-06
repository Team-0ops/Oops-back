package Oops.backend.domain.auth.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.domain.auth.service.AuthService;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Oops.backend.domain.auth.dto.request.ChangePasswordDto;


@Tag(name = "로그인 및 회원가입 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    @PostMapping("/join")
    public ResponseEntity<BaseResponse> join(
            @Valid @RequestBody JoinDto joinDto,
            HttpServletResponse response) {
        this.authService.join(joinDto);
        return BaseResponse.onSuccess(SuccessStatus._CREATED);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 사용하여 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginDto.class)))
            @RequestBody LoginDto loginDto,
            HttpServletResponse response) {

        return BaseResponse.onSuccess(SuccessStatus._OK, authService.login(loginDto, response));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto, @AuthenticatedUser User user) {
        authService.changePassword(user, dto.getOldPassword(), dto.getNewPassword());
        return BaseResponse.onSuccess(SuccessStatus._OK, "비밀번호가 성공적으로 변경되었습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticatedUser User user, HttpServletResponse response) {
        authService.logout(response);
        return BaseResponse.onSuccess(SuccessStatus._OK, "로그아웃 성공");
    }

}
