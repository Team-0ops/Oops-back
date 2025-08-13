package Oops.backend.domain.mypage.dto.response;


import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyProfileResponseDto {
    private String userName;
    private String email;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String profileImageUrl;
    private int point;

    private long commentReportCount;
    private long postReportCount;

    // ✅ 새 버전: presignedUrl을 주입받아 그대로 내려줌 (null 가능)
    public static MyProfileResponseDto from(User user, long commentReportCount, long postReportCount, String presignedUrl) {
        return MyProfileResponseDto.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .profileImageUrl(presignedUrl) // null이면 그대로 null 전달
                .point(user.getPoint() != null ? user.getPoint() : 0)
                .commentReportCount(commentReportCount)
                .postReportCount(postReportCount)
                .build();
    }
}
