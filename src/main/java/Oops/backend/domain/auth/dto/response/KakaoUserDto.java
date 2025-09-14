package Oops.backend.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserDto {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Profile {
            private String nickname;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }

    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.getEmail() : null;
    }

    public String getNickname() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null ? kakaoAccount.getProfile().getNickname() : null;
    }

    public String getProfileImageUrl() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null ? kakaoAccount.getProfile().getProfileImageUrl() : null;
    }
}