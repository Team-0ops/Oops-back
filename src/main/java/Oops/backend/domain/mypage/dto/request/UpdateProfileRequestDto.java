package Oops.backend.domain.mypage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequestDto {

    private String userName;

    //DTO에 유저 이름 들어왔는지 판단
    public boolean hasUserName() {
        return userName != null && !userName.isBlank();
    }
}