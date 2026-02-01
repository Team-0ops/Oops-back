package Oops.backend.domain.auth.controller;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.dto.request.ChangePasswordDto;
import Oops.backend.domain.auth.dto.request.JoinDto;
import Oops.backend.domain.auth.dto.response.LoginResponse;
import Oops.backend.domain.auth.dto.response.TokenResponseDto;
import Oops.backend.domain.auth.service.AuthService;
import Oops.backend.domain.user.dto.request.LoginDto;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


@Tag(name = "로그인 및 회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"COMMON201\",\n" +
                                            "  \"message\": \"SUCCESS!\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "필수 약관 미동의 등으로 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    name = "required_terms_not_agreed",
                                    value = "{\n" +
                                            "  \"isSuccess\": false,\n" +
                                            "  \"code\": \"COMMON400\",\n" +
                                            "  \"message\": \"필수 약관 미동의: [1]\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping("/join")
    public ResponseEntity<BaseResponse> join(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 바디",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JoinDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "join_request_example",
                                            value = """
    {
      "email": "test1@example.com",
      "userName": "홍길동",
      "password": "1234abcd!",
      "verificationToken" : "78730a602f4a44839ccc64ac87c32689"
    }
    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody  JoinDto joinDto,
            HttpServletResponse response
    ) {
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
            description = "이메일을 확인하고 새 비밀번호로 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (이메일 인증 실패 등)")})
    @PostMapping("/reset-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto,  @AuthenticatedUser(required = false) User user) {
        authService.changePassword(user, dto);
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

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse> refresh(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = extractRefreshTokenOnlyFromCookie(request);

        TokenResponseDto tokenDto = authService.refreshAccessToken(refreshToken);

        authService.setCookie(response, tokenDto.getAccessToken());
        authService.setCookieForRefreshToken(response, tokenDto.getRefreshToken());

        return BaseResponse.onSuccess(SuccessStatus._OK, tokenDto);
    }

    private String extractRefreshTokenOnlyFromCookie(HttpServletRequest request) {
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
        if (v.isEmpty()) throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN, "RefreshToken 비어있음");
        return v;
    }

}
