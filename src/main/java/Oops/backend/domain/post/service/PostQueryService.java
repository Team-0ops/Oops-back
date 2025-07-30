package Oops.backend.domain.post.service;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.postGroup.entity.PostGroup;

import java.util.Optional;

public interface PostQueryService {

    Post findPost(Long postId);

    Optional<Post> findPostFromPostGroupBySituation(PostGroup postGroup, Situation situation);

}