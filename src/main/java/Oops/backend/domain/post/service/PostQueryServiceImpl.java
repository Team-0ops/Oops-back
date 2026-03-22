package Oops.backend.domain.post.service;

import Oops.backend.config.s3.S3ImageService;
import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.post.dto.PostDetailSummaryDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostQueryServiceImpl implements PostQueryService{

    private final PostRepository postRepository;
    private final S3ImageService s3ImageService;

    @Override
    public Post findPost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_POST));

        return post;
    }

    @Override
    public Optional<Post> findPostFromPostGroupBySituation(PostGroup postGroup, Situation situation) {

        return postRepository.findPostByPostGroupAndSituation(postGroup, situation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDetailSummaryDto> getMyPosts(User user) {
        return postRepository.findByUser(user).stream()
                .map(post -> {
                    String imageUrl = null;
                    if (post.getImages() != null && !post.getImages().isEmpty()) {
                        try {
                            imageUrl = s3ImageService.getPreSignedUrl(post.getImages().get(0));
                        } catch (Exception e) {
                            imageUrl = null;
                        }
                    }
                    return PostDetailSummaryDto.from(post, imageUrl);
                })
                .toList();
    }
}