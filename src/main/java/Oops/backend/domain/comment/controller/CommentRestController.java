package Oops.backend.domain.comment.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.comment.service.CommentCommandService;
import Oops.backend.domain.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentRestController {

    private final JwtUtil jwtUtil;
    private final CommentCommandService commentCommandService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<BaseResponse> leaveComment(@PathVariable Long postId,
                                                    @RequestHeader("Authorization") String authorization,
                                                    @RequestBody CommentRequestDto.leaveCommentDto request){

        //ToDo : 토큰 검증

        String token = authorization.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        commentCommandService.leaveComment(postId, userId, request);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

}
