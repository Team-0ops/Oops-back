package Oops.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @Schema(description = "사용자 이메일", example = "test123@test.com")
    @NotBlank(message = "이메일이 비었습니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "비밀번호", example = "1234abcd!")
    @NotBlank(message = "비밀번호가 비어있습니다.")
    @Pattern(regexp = "^[A-Za-z0-9]{6,}$", message = "비밀번호 형식이 맞지 않습니다.")
    private String password;
}