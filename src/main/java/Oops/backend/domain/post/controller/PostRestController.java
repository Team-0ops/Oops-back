package Oops.backend.domain.post.controller;


import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.post.dto.PostCreateRequest;
import Oops.backend.domain.post.dto.PostCreateResponse;
import Oops.backend.domain.post.service.PostCommandService;
import Oops.backend.domain.user.entity.User;
import jakarta.validation.Valid;
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
        postCommandService.cheerPost(postId, user);
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



}
