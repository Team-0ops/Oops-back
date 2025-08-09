package Oops.backend.domain.auth.controller;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.JwtEncoder;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.domain.auth.dto.response.LoginResponse;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.service.AuthService;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.GeneratedReferenceTypeDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Oops.backend.domain.auth.dto.request.ChangePasswordDto;


@Tag(name = "로그인 및 회원가입 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtEncoder jwtEncoder;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치 또는 인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginDto.class)))
            @RequestBody LoginDto loginDto,
            HttpServletResponse response) {
        LoginResponse tokenResponseDto = authService.login(loginDto, response);

        return BaseResponse.onSuccess(SuccessStatus._OK, tokenResponseDto);
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "기존 비밀번호를 확인하고 새 비밀번호로 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (기존 비밀번호 불일치 등)")})
    @PostMapping("/reset-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto, @AuthenticatedUser User user) {
        authService.changePassword(user, dto.getOldPassword(), dto.getNewPassword());
        return BaseResponse.onSuccess(SuccessStatus._OK, "비밀번호가 성공적으로 변경되었습니다.");
    }

    @Operation(
            summary = "로그아웃",
            description = "AccessToken 쿠키를 삭제하여 로그아웃 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticatedUser User user, HttpServletResponse response) {
        authService.logout(response);
        return BaseResponse.onSuccess(SuccessStatus._OK, "로그아웃 성공");
    }

    // refreshToken
    @Operation(
            summary = "AccessToken 갱신",
            description = "RefreshToken을 사용하여 새로운 AccessToken을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AccessToken 갱신 성공"),
            @ApiResponse(responseCode = "400", description = "RefreshToken 불일치 또는 인증 실패")
    })
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse> refreshToken(
            HttpServletResponse response, HttpServletRequest request) {

        String refreshToken = getRefreshTokenFromCookie(request);
        TokenResponseDto tokenResponseDto;
        tokenResponseDto = authService.refreshAccessToken(refreshToken);
        authService.setCookie(response, tokenResponseDto.getAccessToken());
        authService.setCookieForRefreshToken(response, tokenResponseDto.getRefreshToken());

        return BaseResponse.onSuccess(SuccessStatus._OK, tokenResponseDto);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        log.info("AuthController: " + request.getCookies());
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("RefreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN, "RefreshToken 파싱 오류");
    }
}
