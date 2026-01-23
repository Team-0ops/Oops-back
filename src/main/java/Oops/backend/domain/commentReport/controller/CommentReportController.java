package Oops.backend.domain.commentReport.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.commentReport.dto.CommentReportRequest;
import Oops.backend.domain.commentReport.service.CommentReportService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "댓글 신고 API")
public class CommentReportController {

    private final CommentReportService commentReportService;

    @Operation(summary = "댓글 신고하기")
    @PostMapping("/comments/{commentId}/reports")
    public ResponseEntity<BaseResponse> reportComment(@PathVariable Long commentId,
                                                      @Parameter(hidden = true) User user,
                                                     @RequestBody CommentReportRequest request){

        log.info("Post /api/comments/{commentId}/reports 호출, User = {}", user.getUserName());

        commentReportService.reportComment(commentId, user, request);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

}
