package Oops.backend.domain.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Getter
@Builder
public class JoinDto {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    @NotBlank(message = "이메일이 비었습니다.")
    @jakarta.validation.constraints.Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @Schema(description = "사용자 닉네임", example = "홍길동")
    @NotBlank(message = "닉네임이 비어있습니다.")
    private String userName;

    @Schema(description = "사용자 비밀번호", example = "1234abcd!")
    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;

    @Schema(description = "사용자 약관 동의 목록")
    @NotNull(message = "약관 동의 정보가 필요합니다.")
    @jakarta.validation.Valid
    private List<AgreeToTermDto> termsAgreement;
}
