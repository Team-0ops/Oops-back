package Oops.backend.domain.auth.dto.response;

import Oops.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

    Long userId;

    String nickname;

    String loginId;

    String accessToken;

    String profileImage;

    private String refreshToken;

    @Builder
    private LoginResponse(Long userId, String nickname, String loginId, String accessToken, String profileImage, String refreshToken){
        this.userId = userId;
        this.nickname = nickname;
        this.loginId = loginId;
        this.accessToken = accessToken;
        this.profileImage = profileImage;
        this.refreshToken = refreshToken;
    }

    public static LoginResponse of(User user, String accessToken, String refreshToken, String profileImage){

        return LoginResponse.builder()
                .userId(user.getId())
                .nickname(user.getUserName())
                .loginId(user.getLoginId())
                .accessToken(accessToken)
                .profileImage(profileImage)
                .refreshToken(refreshToken)
                .build();

    }
}
