package Oops.backend.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateResponse {
    private Long postId;
    private String message;
}
