package Oops.backend.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinDto {

    @Schema(description = "사용자 이메일", example = "test123@test.com")
    @NotBlank(message = "이메일이 비었습니다.")
    @jakarta.validation.constraints.Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @Schema(description = "사용자 닉네임", example = "홍길동")
    @NotBlank(message = "닉네임이 비어있습니다.")
    private String userName;

    @Schema(description = "사용자 비밀번호", example = "1234abcd!")
    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;

    @Schema(description = "회원가입 이메일 인증 토큰", example = "d9f1a2...", required = true)
    @NotBlank(message = "이메일 인증이 필요합니다.")
    private String verificationToken;
}
