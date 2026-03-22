package Oops.backend.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Schema(description = "인증 성공 시 발급되는 verificationToken 응답")
public class VerificationCodeResponseDto {

    @Schema(example = "3a33fd776bba4cab87ea1f13cf841eb9")
    private String verificationToken;
}

