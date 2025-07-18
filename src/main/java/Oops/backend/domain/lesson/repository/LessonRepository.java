package Oops.backend.domain.lesson.repository;

import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // 특정 유저가 작성한 전체 레슨 조회
    List<Lesson> findByUser(User user);

    // 특정 유저의 특정 태그를 포함한 레슨 조회
    @Query("SELECT l FROM Lesson l JOIN l.lessonTags lt WHERE l.user = :user AND lt.name = :tag")
    List<Lesson> findByUserAndTagName(@Param("user") User user, @Param("tag") String tag);
}
