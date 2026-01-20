package Oops.backend.domain.post.controller;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.entity.SortType;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.service.SpecFeedService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "피드 조회 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class SpecFeedController {

    private final SpecFeedService specFeedService;

    /**
     * 베스트 Failers 피드
     */
    @GetMapping("/best/all")
    @Operation(summary = "베스트 Failers 피드 조회 API",description = "베스트 지표에 따라 선정된 베스트 실패담 50개를 정렬 기준에 따라 조회합니다.")
    public ResponseEntity<BaseResponse> getBestPostList(@Parameter(description = "페이지 번호 (0부터 시작)")
                                                            @RequestParam(defaultValue = "0") int page,
                                                        @Parameter(description = "페이지당 게시글 수")
                                                            @RequestParam(defaultValue = "10") int limit,
                                                        @Parameter(description = "정렬 기준 (BEST, LATEST, LIKE, VIEW, COMMENT)")
                                                            @RequestParam(defaultValue = "LATEST") SortType sort) {
        LocalDateTime cutoff = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, limit);

        PostResponse.PostPreviewListDto result = specFeedService.getBestPostList(cutoff, pageable, sort);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 즐겨찾기한  카테고리 피드
     */
    @GetMapping("/bookmarked/{categoryId}/all")
    @Operation(summary = "즐겨찾기한 카테고리 피드 조회 API",description = "즐겨찾기한 카테고리 중 선택된 카테고리의 글 중 요청 situation인 게시글을 정렬 기준에 따라 조회합니다.")
    public ResponseEntity<BaseResponse> getMarkedPostList(@Parameter(description = "카테고리 아이디: 0이면 전체 카테고리 조회입니다.")
                                                              @PathVariable Long categoryId,
                                                          @Parameter(description = "게시글 상태 (OOPS: 웁스중, OVERCOMING: 극복중, OVERCOME: 극복완료)")
                                                              @RequestParam("situation") Situation situation,
                                                          @Parameter(description = "페이지 번호 (0부터 시작)")
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @Parameter(description = "페이지당 게시글 수")
                                                        @RequestParam(defaultValue = "10") int limit,
                                                          @Parameter(hidden = true) @AuthenticatedUser User user,
                                                          @Parameter(description = "정렬 기준 (LATEST, LIKE, VIEW, COMMENT)")
                                                              @RequestParam(defaultValue = "LATEST") SortType sort) {
        if(categoryId<0 || categoryId>15){
            throw new GeneralException(ErrorStatus.INVALID_CATEGORY_ID);
        }

        LocalDateTime cutoff = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, limit);

        PostResponse.PostPreviewListDto result = specFeedService.getMarkedPostList(situation, cutoff, pageable, user, sort, categoryId);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 카테고리별 피드
     */
    @GetMapping("/categories/{categoryId}/all")
    @Operation(summary = "카테고리별 피드 조회 API",description = "선택된 카테고리의 글 중 요청 situation인 게시글을 정렬 기준에 따라 조회합니다.")
    public ResponseEntity<BaseResponse> getMarkedPostList(@PathVariable Long categoryId,
                                                          @Parameter(
                                                                  description = "게시글 상태 (OOPS: 웁스중, OVERCOMING: 극복중, OVERCOME: 극복완료)"
                                                          )
                                                          @RequestParam("situation") Situation situation,
                                                          @Parameter(description = "페이지 번호 (0부터 시작)")
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @Parameter(description = "페이지당 게시글 수")
                                                              @RequestParam(defaultValue = "10") int limit,
                                                          @Parameter(description = "정렬 기준 (LATEST, LIKE, VIEW, COMMENT)")
                                                              @RequestParam(defaultValue = "LATEST") SortType sort) {

        if(categoryId<1 || categoryId>15){
            throw new GeneralException(ErrorStatus.INVALID_CATEGORY_ID);
        }
        LocalDateTime cutoff = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, limit);

        PostResponse.PostPreviewListDto result = specFeedService.getPostByCategoryList(situation, cutoff, pageable, categoryId, sort);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 이번주 랜덤 주제 피드
     */
    @GetMapping("/randomTopic/current/all")
    @Operation(summary = "이번주 랜덤주제 피드 API",description = "이번주 랜덤주제의 글 중 요청 situation인 게시글을 정렬 기준에 따라 조회합니다.")
    public ResponseEntity<BaseResponse> getThisWeekPostList(@Parameter(
                                                                  description = "게시글 상태 (OOPS: 웁스중, OVERCOMING: 극복중, OVERCOME: 극복완료)"
                                                          )
                                                          @RequestParam("situation") Situation situation,
                                                          @Parameter(description = "페이지 번호 (0부터 시작)")
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @Parameter(description = "페이지당 게시글 수")
                                                          @RequestParam(defaultValue = "10") int limit,
                                                            @Parameter(description = "정렬 기준 (LATEST, LIKE, VIEW, COMMENT)")
                                                                @RequestParam(defaultValue = "LATEST") SortType sort) {
        LocalDateTime cutoff = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, limit);

        PostResponse.PostPreviewListDto result = specFeedService.getThisWeekPostList(situation, cutoff, pageable, sort);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 저번주 랜덤 주제 피드
     */
    @GetMapping("/randomTopic/last/all")
    @Operation(summary = "저번주 랜덤주제 피드 API",description = "저번주 랜덤주제 top3 글과 요청 situation인 게시글을 정렬 기준에 따라 조회합니다.")
    public ResponseEntity<BaseResponse> getLastWeekPostList(@Parameter(
                                                                    description = "게시글 상태 (OOPS: 웁스중, OVERCOMING: 극복중, OVERCOME: 극복완료)"
                                                            )
                                                            @RequestParam("situation") Situation situation,
                                                            @Parameter(description = "페이지 번호 (0부터 시작)")
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @Parameter(description = "페이지당 게시글 수")
                                                            @RequestParam(defaultValue = "10") int limit,
                                                            @Parameter(description = "정렬 기준 (LATEST, LIKE, VIEW, COMMENT)")
                                                                @RequestParam(defaultValue = "LATEST") SortType sort) {

        LocalDateTime cutoff = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, limit);

        List<PostResponse.PostPreviewListDto> result = specFeedService.getLastWeekPostList(situation, cutoff, pageable, sort);

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
}
