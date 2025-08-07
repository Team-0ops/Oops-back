package Oops.backend.domain.auth.dto.request;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
public class ChangePasswordDto {

    @Schema(description = "기존 비밀번호", example = "oldPassword123!", required = true)
    private String oldPassword;

    @Schema(description = "새 비밀번호", example = "newSecurePassword123!", required = true)
    private String newPassword;
}
