package Oops.backend.domain.post.service;


import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{

    private final PostRepository postRepository;
    private final PostLikeQueryService postLikeQueryService;
    private final PostLikeCommandService postLikeCommandService;

    @Override
    @Transactional
    public void cheerPost(Long postId, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST, "존재하지 않는 게시글입니다."));

        if (!postLikeQueryService.findPostLike(user, post)){
            post.plusCheer();
            postLikeCommandService.createPostLike(post, user);
        }
        else{
            post.minusCheer();
            postLikeCommandService.deletePostLike(post, user);
        }
    }
}
