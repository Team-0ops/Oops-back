package Oops.backend.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostCreateResponse {
    private Long postId;
    private String message;
    private List<String> imageUrls;
}
