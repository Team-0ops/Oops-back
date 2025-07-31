package Oops.backend.domain.post.dto;

import Oops.backend.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostSummaryDto {
    private Long postId;
    private String title;
    private String situation; // OOPS, OVERCOMING, OVERCOME

    public static PostSummaryDto from(Post post) {
        return PostSummaryDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .situation(post.getSituation().name()) // 그대로 Enum name 사용
                .build();
    }
}