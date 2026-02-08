package Oops.backend.domain.auth.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.dto.request.EmailRequestDto;
import Oops.backend.domain.auth.dto.request.VerificationRequestDto;
import Oops.backend.domain.auth.dto.response.VerificationCodeResponseDto;
import Oops.backend.domain.auth.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Email Verification", description = "이메일 인증코드 발송/검증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email")
public class AuthEmailController {

    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "이메일 인증코드 발송",
            description = "purpose에 따라 회원가입(SIGNUP) / 비밀번호 재설정(PASSWORD_RESET) 인증코드를 이메일로 전송합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EmailRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "회원가입(SIGNUP)",
                                            value = """
                    {
                      "email": "user@example.com",
                      "purpose": "SIGNUP"
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "비밀번호 재설정(PASSWORD_RESET)",
                                            value = """
                    {
                      "email": "user@example.com",
                      "purpose": "PASSWORD_RESET"
                    }
                    """
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전송 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
            {
              "isSuccess": true,
              "code": "OK",
              "message": "입력하신 email 로 인증번호를 전송했습니다.",
              "result": null
            }
            """)
                    )
            )
    })
    @PostMapping("/send")
    public ResponseEntity<BaseResponse> send(@Valid @RequestBody EmailRequestDto emailDto) {
        emailVerificationService.sendCode(emailDto.getEmail(), emailDto.getPurpose());
        return BaseResponse.onSuccess(SuccessStatus._OK, "입력하신 email 로 인증번호를 전송했습니다.");
    }


    @Operation(
            summary = "이메일 인증코드 검증",
            description = "코드가 일치하면 verificationToken(10분 유효)을 발급합니다. 회원가입(join) 요청에 이 토큰을 포함해야 합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = VerificationRequestDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "email": "user@example.com",
                                      "purpose": "SIGNUP",
                                      "code": "123456"
                                    }
                                    """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "검증 성공(토큰 발급)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VerificationCodeResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "CREATED",
                                      "message": "성공",
                                      "result": {
                                        "verificationToken": "3a33fd776bba4cab87ea1f13cf841eb9"
                                      }
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "코드 불일치/만료"),
            @ApiResponse(responseCode = "404", description = "인증 요청 없음"),
            @ApiResponse(responseCode = "403", description = "시도 횟수 초과")
    })
    @PostMapping("/verify")
    public ResponseEntity<BaseResponse> verify(@RequestBody VerificationRequestDto verifyDto) {
        VerificationCodeResponseDto dto =
                new VerificationCodeResponseDto(
                        emailVerificationService.verifyCode(
                                verifyDto.getEmail(),
                                verifyDto.getPurpose(),
                                verifyDto.getCode()
                        )
                );
        return BaseResponse.onSuccess(SuccessStatus._CREATED, dto);
    }

}
