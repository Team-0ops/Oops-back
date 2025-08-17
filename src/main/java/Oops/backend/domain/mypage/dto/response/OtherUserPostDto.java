package Oops.backend.domain.mypage.dto.response;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import lombok.NoArgsConstructor;
/*
@Getter
@Builder
@AllArgsConstructor
public class OtherUserPostDto {
    private Long postId;
    private String title;
    private String content;
    private String categoryOrTopicName;
    private int likeCount;
    private int commentCount;
    private List<String> imageUrls;

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
}*/

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherUserPostDto {
    private Long postId;
    private String title;
    private String content;
    private String categoryOrTopicName;
    private int likeCount;
    private int commentCount;
    private List<String> imageUrls;

    public static OtherUserPostDto from(Post post, java.util.function.Function<String, String> presign) {
        final String categoryOrTopicName;
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

        int likeCount = post.getLikes() == null ? 0 : post.getLikes();
        int commentCount = post.getComments() == null ? 0 : post.getComments().size();

        List<String> imageUrls = (post.getImages() == null) ? List.of()
                : post.getImages().stream()
                .filter(k -> k != null && !k.isBlank())
                .map(k -> {
                    try { return presign.apply(k); } catch (Exception e) { return null; }
                })
                .filter(Objects::nonNull)
                .toList();

        return OtherUserPostDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryOrTopicName(categoryOrTopicName)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .imageUrls(imageUrls)
                .build();
    }
}
