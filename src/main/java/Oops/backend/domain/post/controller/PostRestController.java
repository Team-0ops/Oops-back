package Oops.backend.domain.post.controller;


import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.post.service.PostCommandService;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostCommandService postCommandService;

    @PostMapping("/{postId}/cheers")
    public ResponseEntity<BaseResponse> postCheer(@PathVariable Long postId,
                                                  @AuthenticatedUser User user){
        postCommandService.cheerPost(postId, user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }


}
