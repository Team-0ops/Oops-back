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
    private String topicName;
    private String imageUrl;

    public static PostDetailSummaryDto from(Post post, String imageUrl) {
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
                .topicName(
                        post.getTopic() != null
                                ? post.getTopic().getName()
                                : null
                )
                .imageUrl(imageUrl)
                .build();
    }
}
