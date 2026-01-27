package Oops.backend.domain.auth.dto.request;

import Oops.backend.domain.auth.entity.VerificationPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Schema(description = "이메일 인증코드 검증 요청")
public class VerificationRequestDto {

    @Schema(example = "user@example.com")
    @Email @NotBlank
    private String email;

    @Schema(example = "SIGNUP")
    @NotNull
    private VerificationPurpose purpose;

    @Schema(example = "123456", description = "6자리 인증코드")
    @NotBlank
    private String code;
}


