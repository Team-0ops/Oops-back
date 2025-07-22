package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SpecFeedService {
    // 베스트 피드
    PostResponse.PostPreviewListDto getBestPostList(LocalDateTime cutoff, Pageable pageable);

    // 즐겨찾기 피드
    PostResponse.PostPreviewListDto getMarkedPostList(LocalDateTime cutoff, Pageable pageable, User user);

    // 각 카테고리별 피드
    PostResponse.PostPreviewListDto getPostByCategoryList(LocalDateTime cutoff, Pageable pageable, Long categoryId);

    // 이번주 주제 피드

    // 저번주 주제 피드

}
