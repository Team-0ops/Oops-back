package Oops.backend.domain.auth.dto.response;

import Oops.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

    Long userId;

    String nickname;

    String email;

    String accessToken;

    String profileImage;

    private String refreshToken;

    @Builder
    private LoginResponse(Long userId, String nickname, String email, String accessToken, String profileImage, String refreshToken){
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.accessToken = accessToken;
        this.profileImage = profileImage;
        this.refreshToken = refreshToken;
    }

    public static LoginResponse of(User user, String accessToken, String refreshToken, String profileImage){

        return LoginResponse.builder()
                .userId(user.getId())
                .nickname(user.getUserName())
                .email(user.getEmail())
                .accessToken(accessToken)
                .profileImage(profileImage)
                .refreshToken(refreshToken)
                .build();

    }
}
