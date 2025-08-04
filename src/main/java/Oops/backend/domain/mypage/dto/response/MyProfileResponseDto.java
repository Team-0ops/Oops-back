package Oops.backend.domain.mypage.dto.response;


import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyProfileResponseDto {
    private String userName;
    private String email;
    private String profileImageUrl;
    private int point;

    private long commentReportCount;
    private long postReportCount;

    public static MyProfileResponseDto from(@AuthenticatedUser User user, long commentReportCount, long postReportCount) {
        System.out.println("Dto" + user);
        return MyProfileResponseDto.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .point(user.getPoint() != null ? user.getPoint() : 0) // ✅ null 방어
                .commentReportCount(commentReportCount)
                .postReportCount(postReportCount)
                .build();
    }
}
