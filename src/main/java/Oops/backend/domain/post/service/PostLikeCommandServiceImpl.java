package Oops.backend.domain.post.service;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.entity.PostLike;
import Oops.backend.domain.post.repository.PostLikeRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeCommandServiceImpl implements PostLikeCommandService{

    private final PostLikeRepository postLikeRepository;

    @Override
    public void createPostLike(Post post, User user) {

        PostLike newPostLike = PostLike.createPostLike(post, user);

        postLikeRepository.save(newPostLike);
    }

    @Override
    public void deletePostLike(Post post, User user) {

        Optional<PostLike> postLike = postLikeRepository.findByPostAndUser(post, user);

        postLike.ifPresent(postLikeRepository::delete);
    }
}