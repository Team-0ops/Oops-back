package Oops.backend.domain.postReport.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.post.dto.PostReportRequest;
import Oops.backend.domain.postReport.service.PostReportService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
@Tag(name = "게시글 신고 API")
public class PostReportController {

    private final PostReportService postReportService;

    // 실패담 신고
    @Operation(summary = "게시글 신고하기")
    @PostMapping("/{postId}/reports")
    public ResponseEntity<BaseResponse> reportPost(@AuthenticatedUser User user,
                                                   @PathVariable Long postId,
                                                   @RequestBody PostReportRequest request){

        log.info("Post /api/posts/{postId}/reports 호출, User = {}", user.getUserName());

        postReportService.reportPost(postId, user, request);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }


}
