package Oops.backend.domain.auth.dto.request;

import Oops.backend.domain.auth.entity.VerificationPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이메일 인증코드 발송 요청")public class EmailRequestDto {
    @Schema(example = "user@example.com")
    @Email
    @NotBlank
    private String email;

    @Schema(example = "SIGNUP", description = "SIGNUP 또는 RESET_PASSWORD 등")
    @NotNull
    private VerificationPurpose purpose;
}
