package Oops.backend.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangePasswordDto {

    @Schema(description = "이메일", example = "test@test.com", required = true)
    @NotBlank
    @Email
    private String email;

    @Schema(description = "비밀번호 재설정 토큰(이메일 인증 성공 후 발급)", example = "a1b2c3...", required = true)
    @NotBlank
    private String resetToken;

    @Schema(description = "새 비밀번호", example = "newSecurePassword123!", required = true)
    @NotBlank
    private String newPassword;
}