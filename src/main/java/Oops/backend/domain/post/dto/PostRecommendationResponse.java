package Oops.backend.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostRecommendationResponse {
    private List<PostSummaryDto> similarPosts;
    private List<PostSummaryDto> bestFailers;
}