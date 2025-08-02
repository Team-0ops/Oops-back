package Oops.backend.domain.lesson.service;

import Oops.backend.domain.lesson.dto.response.GetLessonResponse;
import Oops.backend.domain.user.entity.User;

public interface LessonQueryService {

    GetLessonResponse getLesson(User user, Long postId);

}
