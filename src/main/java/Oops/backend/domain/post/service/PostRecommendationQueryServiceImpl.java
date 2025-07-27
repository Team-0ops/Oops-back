package Oops.backend.domain.post.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.post.dto.PostRecommendationResponse;
import Oops.backend.domain.post.dto.PostSummaryDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostRecommendationQueryServiceImpl implements PostRecommendationQueryService {

    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public PostRecommendationResponse recommend(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_POST));

        List<PostSummaryDto> similarPosts;

        if (post.getTopic() != null) {
            similarPosts = postRepository
                    .findTop6ByTopicIdAndIdNotOrderByCreatedAtDesc(post.getTopic().getId(), postId)
                    .stream().map(PostSummaryDto::from).toList();
        } else {
            similarPosts = postRepository
                    .findTop6ByCategoryIdAndIdNotOrderByCreatedAtDesc(post.getCategory().getId(), postId)
                    .stream().map(PostSummaryDto::from).toList();
        }

        List<Situation> bestSituations = List.of(Situation.OOPS,Situation.OVERCOMING, Situation.OVERCOME);
        List<PostSummaryDto> bestFailers = postRepository
                .findBestFailers(bestSituations, PageRequest.of(0, 5))
                .stream()
                .map(PostSummaryDto::from)
                .toList();

        return PostRecommendationResponse.builder()
                .similarPosts(similarPosts)
                .bestFailers(bestFailers)
                .build();
    }
}