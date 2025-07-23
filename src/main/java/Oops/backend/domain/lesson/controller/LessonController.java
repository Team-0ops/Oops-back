package Oops.backend.domain.lesson.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.lesson.dto.request.CreateLessonRequest;
import Oops.backend.domain.lesson.service.LessonCommandService;
import Oops.backend.domain.lesson.service.LessonQueryService;
import Oops.backend.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/posts/{postId}/lessons")
@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonCommandService lessonCommandService;
    private final LessonQueryService lessonQueryService;

    @PostMapping
    public ResponseEntity<BaseResponse> createLesson(@AuthenticatedUser User user,
                                                    Long postId,
                                                    @Valid @RequestBody CreateLessonRequest request){

        lessonCommandService.createLesson(user, postId, request);

        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getLesson(@AuthenticatedUser User user,
                                                  Long postId){

        return BaseResponse.onSuccess(SuccessStatus._OK, lessonQueryService.getLesson(user, postId));
    }

}
