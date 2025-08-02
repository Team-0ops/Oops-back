package Oops.backend.domain.mypage.dto.response;

import Oops.backend.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OtherUserPostDto {
    private String title;
    private String content;
    private String categoryName;
    private int likeCount;
    private int commentCount;

    public static OtherUserPostDto from(Post post) {
        return OtherUserPostDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .categoryName(post.getCategory().getName())
                .likeCount(post.getLikes())
                .commentCount(post.getComments().size()) // 또는 별도 쿼리 사용
                .build();
    }
}