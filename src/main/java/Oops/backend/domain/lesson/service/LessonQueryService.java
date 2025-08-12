package Oops.backend.domain.lesson.service;

import Oops.backend.domain.lesson.dto.response.GetLessonResponse;
import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;

import java.util.List;

public interface LessonQueryService {

    GetLessonResponse getLesson(User user, Long postId);

    List<Lesson> findLessonsByPost(Post post);

}
