package Oops.backend.domain.mypage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequestDto {

    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    private String userName;
}