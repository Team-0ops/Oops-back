package Oops.backend.domain.post.service;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostLikeRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeQueryServiceImpl implements PostLikeQueryService{

    private final PostLikeRepository postLikeRepository;

    @Override
    public boolean findPostLike(User user, Post post) {
        return postLikeRepository.existsPostLikeByPostAndUser(post, user);
    }
}
