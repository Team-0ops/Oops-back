package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.user.entity.User;

import java.util.List;

public interface FeedService {
    List<PostResponse.PostPreviewListDto> getFirstPostList(User user);
    PostResponse.PostPreviewListDto getLaterPostList();
}
