package Oops.backend.domain.post.service;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;

public interface PostLikeCommandService {

    void createPostLike(Post post, User user);

    void deletePostLike(Post post, User user);

}