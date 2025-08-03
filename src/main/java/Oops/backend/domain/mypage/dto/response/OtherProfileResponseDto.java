package Oops.backend.domain.mypage.dto.response;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OtherProfileResponseDto {
    private String userName;
    private List<OtherUserPostDto> posts;

    public static OtherProfileResponseDto from(User user, List<Post> posts) {
        return OtherProfileResponseDto.builder()
                .userName(user.getUserName())
                .posts(posts.stream().map(OtherUserPostDto::from).toList())
                .build();
    }
}
