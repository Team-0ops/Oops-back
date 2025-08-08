package Oops.backend.domain.lesson.repository;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByUser(User user);

    @Query("SELECT l FROM Lesson l " +
            "JOIN l.tags lt " +
            "JOIN lt.tag t " +
            "WHERE l.user = :user AND t.name = :tag")
    List<Lesson> findByUserAndTagName(@Param("user") User user,
                                      @Param("tag") String tag);

    boolean existsLessonByUserAndPost(User user, Post post);

    Optional<Lesson> findByUserAndPost(User user, Post post);

    void deleteAllByPost(Post post);

    List<Lesson> findLessonsByPost(Post post);
}