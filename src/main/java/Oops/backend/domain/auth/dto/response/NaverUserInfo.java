package Oops.backend.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NaverUserInfo {
    private final String id;
    private final String email;
    private final String nickname;
    private final String profileImage;
}