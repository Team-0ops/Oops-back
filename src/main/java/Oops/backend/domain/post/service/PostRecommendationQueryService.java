package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostRecommendationResponse;
import Oops.backend.domain.user.entity.User;

public interface PostRecommendationQueryService {
    PostRecommendationResponse recommend(User user, Long postId);
}
