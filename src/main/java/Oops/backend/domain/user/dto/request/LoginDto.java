package Oops.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginDto {

    @Schema(description = "사용자 아이디", example = "test123")
    @NotBlank(message = "이메일이 비었습니다.")
    @Pattern(regexp = "^[A-Za-z0-9]{6,}$", message = "아이디 형식이 맞지 않습니다.")
    private String LoginId;

    @Schema(description = "비밀번호", example = "1234abcd!")
    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
}