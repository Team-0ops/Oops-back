package Oops.backend.domain.mypage.dto.response;

import Oops.backend.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
public class MyPostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String categoryName;
    private int likes;
    private int watching;

    public static MyPostResponseDto from(Post post) {
        System.out.println("Dto" + post);
        return MyPostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryName(post.getCategory().getName())
                .likes(post.getLikes())
                .watching(post.getWatching())
                .build();
    }
}
