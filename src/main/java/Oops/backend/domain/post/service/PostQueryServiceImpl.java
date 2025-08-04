package Oops.backend.domain.post.service;

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
                .map(PostDetailSummaryDto::from)
                .toList();
    }
}