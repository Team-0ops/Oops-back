package Oops.backend.domain.auth.dto.response;

import Oops.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

    String nickname;

    String email;

    String accessToken;

    String profileImage;

    @Builder
    private LoginResponse(String nickname, String email, String accessToken, String profileImage){
        this.nickname = nickname;
        this.email = email;
        this.accessToken = accessToken;
        this.profileImage = profileImage;
    }

    public static LoginResponse of(User user, String accessToken){

        return LoginResponse.builder()
                .nickname(user.getUserName())
                .email(user.getEmail())
                .accessToken(accessToken)
                .profileImage(user.getProfileImageUrl())
                .build();

    }
}
