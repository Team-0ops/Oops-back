package Oops.backend.domain.post.controller;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.service.SpecFeedService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class SpecFeedController {

    private final SpecFeedService specFeedService;

    /**
     * 베스트 Failers 피드
     */
    @GetMapping("/best/all")
    @Operation(summary = "베스트 Failers 피드 조회 API",description = "실패담을 베스트 지표에 따라 정렬하여 보여줍니다.")
    public ResponseEntity<BaseResponse> getBestPostList(@Parameter(description = "페이지 번호 (0부터 시작)")
                                                            @RequestParam(defaultValue = "0") int page,
                                                        @Parameter(description = "페이지당 게시글 수")
                                                            @RequestParam(defaultValue = "10") int limit) {
        LocalDateTime cutoff = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, limit);

        PostResponse.PostPreviewListDto result = specFeedService.getBestPostList(cutoff, pageable);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 즐겨찾기한  카테고리 피드
     */
    @GetMapping("/bookmarked/all")
    @Operation(summary = "즐겨찾기한 카테고리 피드 조회 API",description = "즐겨찾기한 카테고리의 글 중 요청 상태인 게시글을 최신순으로 조회합니다.")
    public ResponseEntity<BaseResponse> getMarkedPostList(@Parameter(
                                                                      description = "게시글 상태 (OOPS: 웁스중, OVERCOMING: 극복중, OVERCOME: 극복완료)"
                                                              )
                                                              @RequestParam("situation") Situation situation,
                                                          @Parameter(description = "페이지 번호 (0부터 시작)")
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @Parameter(description = "페이지당 게시글 수")
                                                        @RequestParam(defaultValue = "10") int limit,
                                                          @Parameter(hidden = true) @AuthenticatedUser User user) {
        LocalDateTime cutoff = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        PostResponse.PostPreviewListDto result = specFeedService.getMarkedPostList(situation, cutoff, pageable, user);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 카테고리별 피드
     */
    @GetMapping("/categories/{categoryId}/all")
    @Operation(summary = "카테고리별 피드 조회 API",description = "선택된 카테고리의 글 중 요청 상태인 게시글을 최신순으로 조회합니다.")
    public ResponseEntity<BaseResponse> getMarkedPostList(@PathVariable Long categoryId,
                                                          @Parameter(
                                                                  description = "게시글 상태 (OOPS: 웁스중, OVERCOMING: 극복중, OVERCOME: 극복완료)"
                                                          )
                                                          @RequestParam("situation") Situation situation,
                                                          @Parameter(description = "페이지 번호 (0부터 시작)")
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @Parameter(description = "페이지당 게시글 수")
                                                              @RequestParam(defaultValue = "10") int limit) {
        if(categoryId<1 || categoryId>15){
            throw new GeneralException(ErrorStatus.INVALID_CATEGORY_ID);
        }
        LocalDateTime cutoff = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        PostResponse.PostPreviewListDto result = specFeedService.getPostByCategoryList(situation, cutoff, pageable, categoryId);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

}
