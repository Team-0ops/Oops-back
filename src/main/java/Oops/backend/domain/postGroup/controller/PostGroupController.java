package Oops.backend.domain.postGroup.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.postGroup.service.PostGroupQueryService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostGroupController {

    private final PostGroupQueryService postGroupQueryService;

    @GetMapping("/{postId}")
    @Operation(summary = "실패담 상세 조회 API")
    public ResponseEntity<BaseResponse> getPostGroup(@AuthenticatedUser User user,
                                                     @PathVariable Long postId){

        return BaseResponse.onSuccess(SuccessStatus._OK, postGroupQueryService.getPostGroup(user, postId));
    }

}
