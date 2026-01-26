package Oops.backend.domain.comment.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.comment.entity.SortType;
import Oops.backend.domain.comment.service.CommentCommandService;
import Oops.backend.domain.comment.service.CommentQueryService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentRestController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @Operation(summary = "댓글 달기")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<BaseResponse> leaveComment(@PathVariable("postId") Long postId,
                                                     @Parameter(hidden = true) @AuthenticatedUser User user,
                                                     @Valid @RequestBody CommentRequestDto.LeaveCommentDto request){

        log.info("Post /api/{postId}/comments 호출, User = {}", user.getUserName());

        commentCommandService.leaveComment(postId, user, request);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "댓글 삭제하기")
    @DeleteMapping("posts/{postId}/comments/{commentId}")
    public ResponseEntity<BaseResponse> deleteComment(@PathVariable("postId") Long postId,
                                                      @PathVariable("commentId") Long commentId,
                                                      @Parameter(hidden = true)@AuthenticatedUser  User user){

        log.info("Delete /api/{postId}/comments/{commentId} 호출, User = {}", user.getUserName());

        commentCommandService.deleteComment(postId, commentId, user);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "댓글 좋아요 누르기")
    @PostMapping("/comments/{commentId}/cheers")
    public ResponseEntity<BaseResponse> cheerComment(@PathVariable Long commentId,
                                                     @Parameter(hidden = true)@AuthenticatedUser  User user){

        log.info("Post /api/comments/{commentId}/cheers 호출, User = {}", user.getUserName());

        commentCommandService.cheerComment(commentId, user);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "게시글에 대한 댓글 조회")
    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<BaseResponse> getCommentsOfPost(
            @Parameter(name = "postId", description = "게시글 ID", required = true, example = "1")
            @PathVariable("postId") Long postId,
            @Parameter(hidden = true) @AuthenticatedUser User user,
            @Parameter(name = "sortType", description = "정렬 타입 (LIKE: 좋아요순, RECENT: 최신순)", required = true, example = "LIKE")
            @RequestParam("sortType") SortType sortType){

        return BaseResponse.onSuccess(SuccessStatus._OK, commentQueryService.findCommentsOfPost(postId, user, sortType));
    }


}
