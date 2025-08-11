package Oops.backend.domain.mypage.dto.response;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
public class MyPostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String categoryOrTopicName;
    private int likes;
    private int watching;

    public static MyPostResponseDto from(Post post) {
        String categoryOrTopicName;

        if (post.getCategory() != null && post.getTopic() == null) {
            categoryOrTopicName = post.getCategory().getName();
        } else if (post.getCategory() == null && post.getTopic() != null) {
            categoryOrTopicName = post.getTopic().getName();
        } else {
            throw new GeneralException(
                    ErrorStatus.POST_CATEGORY_TOPIC_INVALID,
                    "카테고리 / 랜덤 주제 설정이 잘못된 게시글입니다."
            );
        }
        System.out.println("Dto" + post);
        return MyPostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryOrTopicName(categoryOrTopicName)
                .likes(post.getLikes())
                .watching(post.getWatching())
                .build();
    }
}

