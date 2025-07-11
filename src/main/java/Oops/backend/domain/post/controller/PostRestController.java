package Oops.backend.domain.post.controller;


import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.jwt.JwtUtil;
import Oops.backend.domain.post.service.PostCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostCommandService postCommandService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{postId}/cheers")
    public ResponseEntity<BaseResponse> postCheer(@PathVariable Long postId,
                                                  @RequestHeader("Authorization") String authorization){

        //ToDo : 토큰 검증 로직 추가

        String token = authorization.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        postCommandService.cheerPost(postId, userId);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }


}
