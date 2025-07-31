package Oops.backend.domain.post.controller;


import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.post.dto.PostCreateRequest;
import Oops.backend.domain.post.dto.PostCreateResponse;
import Oops.backend.domain.post.dto.PostRecommendationResponse;
import Oops.backend.domain.post.dto.PostSummaryDto;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.service.PostCommandService;
import Oops.backend.domain.post.service.PostRecommendationQueryService;
import Oops.backend.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostCommandService postCommandService;
    private final PostRecommendationQueryService postRecommendationQueryService;

    @PostMapping("/{postId}/cheers")
    public ResponseEntity<BaseResponse> postCheer(@PathVariable Long postId,
                                                  @AuthenticatedUser User user){
        postCommandService.cheerPost(postId, user);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse> deletePost(@PathVariable Long postId,
                                                   @AuthenticatedUser User user){
        postCommandService.deletePost(postId, user);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    //실패담 작성
    @PostMapping
    public ResponseEntity<BaseResponse> createPost(
            @AuthenticatedUser User user,
            @RequestBody @Valid PostCreateRequest request) {

        PostCreateResponse response = postCommandService.createPost(user, request);
        return BaseResponse.onSuccess(SuccessStatus._CREATED, response);
    }
    // 상황에 따라 연결 가능한 이전 글 목록 조회
    @GetMapping("/previous")
    public ResponseEntity<BaseResponse> getPreviousPostsForSituation(
            @AuthenticatedUser User user,
            @RequestParam("situation") Situation situation) {

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

    //실패담 추천
    // PostRestController.java

    @GetMapping("/{postId}/recommendations")
    public ResponseEntity<BaseResponse> recommendPosts(
            @AuthenticatedUser User user,
            @PathVariable Long postId) {

        PostRecommendationResponse response = postRecommendationQueryService.recommend(user, postId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }




}
