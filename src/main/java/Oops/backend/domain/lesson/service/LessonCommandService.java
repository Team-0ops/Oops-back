package Oops.backend.domain.lesson.service;

import Oops.backend.domain.lesson.dto.request.CreateLessonRequest;
import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;

public interface LessonCommandService {

    Lesson createLesson(User user, Long postId, CreateLessonRequest request);

    void deleteAllLessonsOfPost(Post post);

}