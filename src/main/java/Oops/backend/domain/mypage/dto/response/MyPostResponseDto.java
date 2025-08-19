package Oops.backend.domain.mypage.dto.response;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;


@Getter
@Builder
public class MyPostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String categoryOrTopicName;
    private String situation;
    private int likes;
    private int watching;
    private List<String> imageUrls;

    public static MyPostResponseDto from(Post post) {
        return fromWithImages(post, List.of());
    }
    public static MyPostResponseDto fromWithImages(Post post, List<String> imageUrls) {
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
                .situation(post.getSituation().name())
                .categoryOrTopicName(categoryOrTopicName)
                .likes(post.getLikes())
                .watching(post.getWatching())
                .imageUrls(imageUrls == null ? List.of() : imageUrls)
                .build();
    }
    public static MyPostResponseDto from(Post post, Function<String, String> presign) {
        List<String> urls = (post.getImages() == null || post.getImages().isEmpty())
                ? List.of()
                : post.getImages().stream()
                .map(k -> {
                    try { return presign.apply(k); } catch (Exception e) { return null; }
                })
                .filter(Objects::nonNull)
                .toList();

        return fromWithImages(post, urls);
    }
}

