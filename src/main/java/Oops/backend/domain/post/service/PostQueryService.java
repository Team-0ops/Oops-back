package Oops.backend.domain.post.service;

import Oops.backend.domain.post.entity.Post;

public interface PostQueryService {

    Post findPost(Long postId);

}