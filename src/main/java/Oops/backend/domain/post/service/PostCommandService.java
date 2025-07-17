package Oops.backend.domain.post.service;

import Oops.backend.domain.user.entity.User;

public interface PostCommandService {

    void cheerPost(Long postId, User user);
}
