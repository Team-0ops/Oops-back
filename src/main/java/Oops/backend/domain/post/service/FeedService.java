package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedService {
    List<PostResponse.PostPreviewListDto> getFirstPostList(User user);
    PostResponse.PostPreviewListDto getLaterPostList();
    PostResponse.PostPreviewListDto searchPosts(String keyword, Pageable pageable);
}
