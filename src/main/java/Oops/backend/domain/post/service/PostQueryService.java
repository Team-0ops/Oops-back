package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostDetailSummaryDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface PostQueryService {

    Post findPost(Long postId);

    Optional<Post> findPostFromPostGroupBySituation(PostGroup postGroup, Situation situation);

    List<PostDetailSummaryDto> getMyPosts(User user);

}