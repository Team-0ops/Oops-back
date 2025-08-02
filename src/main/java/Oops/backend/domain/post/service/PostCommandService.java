package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostCreateRequest;
import Oops.backend.domain.post.dto.PostCreateResponse;
import Oops.backend.domain.user.entity.User;

public interface PostCommandService {

    void cheerPost(Long postId, User user);
    void deletePost(Long postId, User user);

    PostCreateResponse createPost(User user, PostCreateRequest request);
}
