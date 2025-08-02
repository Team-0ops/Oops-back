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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class HomeFeedController {
    private final HomeFeedService feedService;

    /**
     * 홈화면 첫 로딩 - 로그인 O
     */
    @GetMapping("/home/first-auth")
    @Operation(summary = "로그인한 경우 홈화면 첫 로딩 API",description = "홈화면 처음 로딩 시 필요한 베스트 실패담 5개와 즐겨찾기한 실패담 10개만 우선 조회합니다.")
    public ResponseEntity<BaseResponse> getFirstPostList(@Parameter(hidden = true) @AuthenticatedUser User user) {

        List<PostResponse.PostPreviewListDto> result = feedService.getFirstPostList(user);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 홈화면 첫 로딩 - 로그인 X
     */
    @GetMapping("/home/first-guest")
    @Operation(summary = "로그인하지 않은 경우 홈화면 첫 로딩 API",description = "홈화면 처음 로딩 시 필요한 베스트 실패담 5개를 조회합니다. 즐찾 리스트는 null ")
    public ResponseEntity<BaseResponse> getFirstPostList() {

        List<PostResponse.PostPreviewListDto> result = feedService.getFirstPostListForGuest();
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 홈 화면 이후 로딩
     */
    @GetMapping("/home/later")
    @Operation(summary = "홈화면 이후 로딩 API",description ="카테고리별 최신글 하나씩 조회합니다.")
    public ResponseEntity<BaseResponse> getLaterPostList() {
        PostResponse.PostPreviewListDto result = feedService.getLaterPostList();
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

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        PostResponse.PostPreviewListDto result = feedService.searchPosts(keyword, pageable);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
}
