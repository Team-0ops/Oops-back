package Oops.backend.domain.comment.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.comment.service.CommentCommandService;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentRestController {

    private final CommentCommandService commentCommandService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<BaseResponse> leaveComment(@PathVariable Long postId,
                                                     @AuthenticatedUser User user,
                                                     @RequestBody CommentRequestDto.LeaveCommentDto request){

        commentCommandService.leaveComment(postId, user, request);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<BaseResponse> deleteComment(@PathVariable Long postId,
                                                      @PathVariable Long commentId,
                                                      @AuthenticatedUser User user){

        commentCommandService.deleteComment(postId, commentId, user);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

}
