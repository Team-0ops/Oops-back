package Oops.backend.domain.post.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.auth.AuthenticationContext;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.service.HomeFeedService;
import Oops.backend.domain.randomTopic.Service.RandomTopicService;
import Oops.backend.domain.randomTopic.dto.RandomTopicResponse;
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
    private final RandomTopicService randomTopicService;

    /**
     * 상단 배너 정보 조회
     */
    @GetMapping("/home/banners")
    @Operation(summary = "홈화면 배너 API",description = "홈화면의 배너에 필요한 정보를 조회하는 api입니다. ")
    public ResponseEntity<BaseResponse> getBannarInfo (@Parameter(hidden = true) @AuthenticatedUser User user) {

        RandomTopicResponse.BannarsInfoDto result = randomTopicService.getBannarInfo(user);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 베스트 실패담 5개 조회
     */
    @GetMapping("/home/best")
    @Operation(summary = "홈화면 베스트 실패담 조회 API",description = "베스트 실패담 5개를 조회합니다.")
    public ResponseEntity<BaseResponse> getBestPostList(@Parameter(hidden = true)
                                                            @AuthenticatedUser(required = false) User user) {
        if (user == null){
            PostResponse.PostPreviewListDto result1 = feedService.getBestPostList(null);
            return BaseResponse.onSuccess(SuccessStatus._OK, result1);

        }else{
            PostResponse.PostPreviewListDto result2 = feedService.getBestPostList(user);
            return BaseResponse.onSuccess(SuccessStatus._OK, result2);
        }

    }

    /**
     * 즐겨찾기한 카테고리 중 하나의 실패담 5개 조회
     */
    @GetMapping("/home/bookmarked")
    @Operation(summary = "홈화면 특정 즐겨찾기 카테고리의 실패담 조회 API",description = "즐겨찾기한 카테고리 중 사용자가 선택한 카테고리의 실패담 5개를 조회합니다.")
    public ResponseEntity<BaseResponse> getBookmarkedPostList(@Parameter(hidden = true) @AuthenticatedUser User user,
                                                              @Parameter(description = "카테고리 아이디: 0이면 전체 카테고리 조회입니다.")
                                                              @RequestParam Long categoryId) {

        PostResponse.PostPreviewListDto result = feedService.getBookmarkedPostList(user, categoryId);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 카테고리별 최신글 1개씩 조회
     */
    @GetMapping("/home/categories")
    @Operation(summary = "카테고리별 최신글 1개씩 조회 API",description ="카테고리별 최신글 하나씩 조회합니다.")
    public ResponseEntity<BaseResponse> getCategoriesPostList(@Parameter(hidden = true) @AuthenticatedUser User user) {

        PostResponse.PostPreviewListDto result = feedService.getCategoriesPostList(user);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 실패담 검색
     */
    @GetMapping("/search")
    @Operation(summary = "실패담 검색 API",description ="검색어가 제목, 내용, 카테고리에 포함된 모든 글들을 최신순으로 조회한다. 페이지는 0부터 시작한다.")
    public ResponseEntity<BaseResponse> getAllPostsByKeyword(@Parameter(hidden = true) @AuthenticatedUser User user,
                                                             @RequestParam String keyword,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int limit) {

        log.info("Get /api/feeds/search 호출");

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        PostResponse.PostPreviewListDto result = feedService.searchPosts(user, keyword, pageable);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
}