package Oops.backend.domain.lesson.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.lesson.dto.request.CreateLessonRequest;
import Oops.backend.domain.lesson.service.LessonCommandService;
import Oops.backend.domain.lesson.service.LessonQueryService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "교훈 관련 API")
@Slf4j
@RequestMapping("/api/posts/{postId}/lessons")
@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonCommandService lessonCommandService;
    private final LessonQueryService lessonQueryService;

    @Operation(summary = "교훈 작성 API")
    @PostMapping
    public ResponseEntity<BaseResponse> createLesson(@Parameter(hidden = true) @AuthenticatedUser User user,
                                                    @PathVariable Long postId,
                                                    @Valid @RequestBody CreateLessonRequest request){

        log.info("Post /api/posts/lessons 호출, User = {}", user.getUserName());

        lessonCommandService.createLesson(user, postId, request);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "교훈 조회 API")
    @GetMapping
    public ResponseEntity<BaseResponse> getLesson(@Parameter(hidden = true) @AuthenticatedUser User user,
                                                  @PathVariable Long postId){

        log.info("Get /api/posts/lessons 호출, User = {}", user.getUserName());

        return BaseResponse.onSuccess(SuccessStatus._OK, lessonQueryService.getLesson(user, postId));
    }

}
