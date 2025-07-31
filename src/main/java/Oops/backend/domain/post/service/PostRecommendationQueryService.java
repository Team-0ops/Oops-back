package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostRecommendationResponse;
import Oops.backend.domain.post.dto.PostSummaryDto;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.user.entity.User;

import java.util.List;

public interface PostRecommendationQueryService {
    PostRecommendationResponse recommend(User user, Long postId);

    List<PostSummaryDto> getMyPostsBySituation(User user, Situation situation);
}
