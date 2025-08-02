package Oops.backend.domain.post.dto;

import Oops.backend.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostDetailSummaryDto {

    private Long postId;
    private String title;
    private String situation;
    private String content;
    private String categoryName;
    private String imageUrl;

    public static PostDetailSummaryDto from(Post post) {
        return PostDetailSummaryDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .situation(post.getSituation().name())
                .content(post.getContent())
                .categoryName(
                        post.getCategory() != null
                                ? post.getCategory().getName()
                                : null
                )
                .imageUrl(
                        post.getImages() != null && !post.getImages().isEmpty()
                                ? post.getImages().get(0) // 첫 번째 이미지 URL
                                : null
                )
                .build();
    }
}
