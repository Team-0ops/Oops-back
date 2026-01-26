package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HomeFeedService {
    PostResponse.PostPreviewListDto getBestPostList(User user);
    PostResponse.PostPreviewListDto getBookmarkedPostList(User user, Long categoryId);
    PostResponse.PostPreviewListDto getCategoriesPostList(User user);
    PostResponse.PostPreviewListDto searchPosts(User user, String keyword, Pageable pageable);
}
