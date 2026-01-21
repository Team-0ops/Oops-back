package Oops.backend.domain.auth.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinDto {

    @Schema(description = "사용자 아이디", example = "test123!")
    @NotBlank(message = "아이디가 비었습니다.")
    @jakarta.validation.constraints.Email(message = "아이디 형식이 맞지 않습니다.")
    private String loginId;

    @Schema(description = "사용자 닉네임", example = "홍길동")
    @NotBlank(message = "닉네임이 비어있습니다.")
    private String userName;

    @Schema(description = "사용자 비밀번호", example = "1234abcd!")
    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
}
