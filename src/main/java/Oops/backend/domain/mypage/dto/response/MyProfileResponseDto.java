package Oops.backend.domain.mypage.dto.response;


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

    public static MyProfileResponseDto from(User user, long commentReportCount, long postReportCount, String presignedUrl) {
        return MyProfileResponseDto.builder()
                .userName(user.getEmail())
                .profileImageUrl(presignedUrl)
                .point(user.getPoint() != null ? user.getPoint() : 0)
                .commentReportCount(commentReportCount)
                .postReportCount(postReportCount)
                .build();
    }
}
