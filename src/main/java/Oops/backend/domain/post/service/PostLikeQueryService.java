package Oops.backend.domain.post.service;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;

public interface PostLikeQueryService {

    boolean findPostLike(User user, Post post);

}
