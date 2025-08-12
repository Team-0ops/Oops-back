package Oops.backend.domain.mypage.dto.response;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
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
    private String categoryOrTopicName;
    private int likeCount;
    private int commentCount;

    public static OtherUserPostDto from(Post post) {
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
        return OtherUserPostDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .categoryOrTopicName(categoryOrTopicName)
                .likeCount(post.getLikes())
                .commentCount(post.getComments().size()) // 또는 별도 쿼리 사용
                .build();
    }
}