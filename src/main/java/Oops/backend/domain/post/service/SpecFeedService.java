package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.entity.SortType;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SpecFeedService {
    // 베스트 피드
    PostResponse.PostPreviewListDto getBestPostList(LocalDateTime cutoff, Pageable pageable, SortType sort);

    // 즐겨찾기 피드
    PostResponse.PostPreviewListDto getMarkedPostList(Situation situation, LocalDateTime cutoff, Pageable pageable, User user, SortType sort, Long categoryId);

    // 각 카테고리별 피드
    PostResponse.PostPreviewListDto getPostByCategoryList(Situation situation, LocalDateTime cutoff, Pageable pageable, Long categoryId, SortType sort);

    // 이번주 주제 피드
    PostResponse.PostPreviewListDto getThisWeekPostList(Situation situation, LocalDateTime cutoff, Pageable pageable, SortType sort);

    // 저번주 주제 피드
    List<PostResponse.PostPreviewListDto> getLastWeekPostList(Situation situation, LocalDateTime cutoff, Pageable pageable, SortType sort);
}
