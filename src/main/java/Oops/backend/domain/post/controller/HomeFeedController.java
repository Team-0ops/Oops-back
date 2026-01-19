package Oops.backend.domain.post.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.AuthenticationContext;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.service.HomeFeedService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "메인페이지 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class HomeFeedController {
    private final HomeFeedService feedService;

    /**
     * 베스트 실패담 5개 조회
     */
    @GetMapping("/home/best")
    @Operation(summary = "홈화면 베스트 실패담 조회 API",description = "베스트 실패담 5개를 조회합니다.")
    public ResponseEntity<BaseResponse> getBestPostList(@Parameter(hidden = true) @AuthenticatedUser User user) {

        PostResponse.PostPreviewListDto result = feedService.getBestPostList(user);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 즐겨찾기한 카테고리 중 하나의 실패담 10개 조회
     */
    @GetMapping("/home/bookmarked")
    @Operation(summary = "홈화면 특정 즐겨찾기 카테고리의 실패담 조회 API",description = "즐겨찾기한 카테고리 중 사용자가 선택한 카테고리의 실패담 10개를 조회합니다.")
    public ResponseEntity<BaseResponse> getBookmarkedPostList(@Parameter(hidden = true) @AuthenticatedUser User user) {

        PostResponse.PostPreviewListDto result = feedService.getBookmarkedPostList(user);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 카테고리별 최신글 1개씩 조회
     */
    @GetMapping("/home/categories")
    @Operation(summary = "카테고리별 최신글 1개씩 조회 API",description ="카테고리별 최신글 하나씩 조회합니다.")
    public ResponseEntity<BaseResponse> getCategoriesPostList() {

        PostResponse.PostPreviewListDto result = feedService.getCategoriesPostList();
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 실패담 검색
     */
    @GetMapping("/search")
    @Operation(summary = "실패담 검색 API",description ="검색어가 제목, 내용, 카테고리에 포함된 모든 글들을 최신순으로 조회한다. 페이지는 0부터 시작한다.")
    public ResponseEntity<BaseResponse> getAllPostsByKeyword(@RequestParam String keyword,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int limit) {

        log.info("Get /api/feeds/search 호출");

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        PostResponse.PostPreviewListDto result = feedService.searchPosts(keyword, pageable);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
}