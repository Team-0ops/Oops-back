package Oops.backend.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginDto {
    @NotBlank(message = "이메일이 비었습니다.")
    @Pattern(regexp = "^[A-Za-z0-9]+@[A-Za-z0-9]+.[A-Za-z]{2,6}$", message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호기 비어있습니다.")
    private String password;
}
