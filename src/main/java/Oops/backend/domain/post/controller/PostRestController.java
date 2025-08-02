package Oops.backend.domain.post.controller;


import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.post.dto.*;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.service.PostCommandService;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.post.service.PostRecommendationQueryService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(name = "실패담 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostCommandService postCommandService;

    private final PostRecommendationQueryService postRecommendationQueryService;
    private final PostQueryService postQueryService;

    @Operation(summary = "응원하기 API")
    @PostMapping("/{postId}/cheers")
    public ResponseEntity<BaseResponse> postCheer(@PathVariable Long postId,
                                                  @AuthenticatedUser User user){

        log.info("Post /api/posts/{postId}/cheers 호출, User = {}", user.getUserName());

        postCommandService.cheerPost(postId, user);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "실패담 삭제 API")
    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse> deletePost(@PathVariable Long postId,
                                                   @AuthenticatedUser User user){

        log.info("Delete /api/posts/{postId} 호출, User = {}", user.getUserName());

        postCommandService.deletePost(postId, user);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    //실패담 작성
    @Operation(summary = "실패담 작성", description = "새로운 실패담을 작성합니다. 상황(OOPS, OVERCOMING, OVERCOME)")
    @PostMapping
    public ResponseEntity<BaseResponse> createPost(
            @AuthenticatedUser User user,
            @RequestBody @Valid PostCreateRequest request) {

        log.info("Post /api/posts 호출, User = {}", user.getUserName());

        PostCreateResponse response = postCommandService.createPost(user, request);
        return BaseResponse.onSuccess(SuccessStatus._CREATED, response);
    }

    // [추가] 내가 작성한 전체 실패담 조회 API
    @Operation(summary = "내가 작성한 실패담 조회", description = "로그인한 사용자가 작성한 모든 실패담을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<BaseResponse> getMyPosts(@AuthenticatedUser User user) {
        List<PostDetailSummaryDto> response = postQueryService.getMyPosts(user);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    
/*
    // 상황에 따라 연결 가능한 이전 글 목록 조회
    @Operation(summary = "연경 가능한 이전 글 목록 조회")
    @GetMapping("/previous")
    public ResponseEntity<BaseResponse> getPreviousPostsForSituation(
            @AuthenticatedUser User user,
            @RequestParam("situation") Situation situation) {

        log.info("Get /api/posts/previous 호출, User = {}", user.getUserName());

        List<PostSummaryDto> result;

        if (situation == Situation.OVERCOMING) {
            result = postRecommendationQueryService.getMyPostsBySituation(user, Situation.OOPS);
        } else if (situation == Situation.OVERCOME) {
            result = postRecommendationQueryService.getMyPostsBySituation(user, Situation.OVERCOMING);
        } else {
            // OOPS 상황에서는 이전 글이 필요 없으므로 204 반환
            return BaseResponse.onSuccess(SuccessStatus._OK, Collections.emptyList());
        }

        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
*/

    //실패담 추천
    // PostRestController.java
    @Operation(summary = "실패담 추천 조회", description = "특정 실패담과 같은 카테고리의 실패담과 베스트 실패담을 추천해줍니다.")
    @GetMapping("/{postId}/recommendations")
    public ResponseEntity<BaseResponse> recommendPosts(
            @AuthenticatedUser User user,
            @PathVariable Long postId) {

        log.info("Get /api/posts/{postId}/recommendations 호출, User = {}", user.getUserName());

        PostRecommendationResponse response = postRecommendationQueryService.recommend(user, postId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }




}
