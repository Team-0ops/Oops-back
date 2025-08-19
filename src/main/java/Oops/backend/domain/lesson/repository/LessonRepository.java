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

    // ✅ 내 교훈 전체 조회 (삭제된 게시글도 포함: post가 null인 Lesson도 포함됨)
    @Query("""
        SELECT DISTINCT l FROM Lesson l
        LEFT JOIN FETCH l.post p
        LEFT JOIN FETCH p.category c
        LEFT JOIN FETCH p.topic t
        LEFT JOIN FETCH l.tags lt
        LEFT JOIN FETCH lt.tag tg
        WHERE l.user = :user
        ORDER BY l.id DESC
    """)
    List<Lesson> findByUserWithPostAndTags(@Param("user") User user);

    // ✅ 태그 필터 조회 (필터 자체는 태그에 대해 INNER JOIN, 게시글 fetch는 LEFT로)
    @Query("""
        SELECT DISTINCT l FROM Lesson l
        LEFT JOIN FETCH l.post p
        LEFT JOIN FETCH p.category c
        LEFT JOIN FETCH p.topic t
        JOIN l.tags lt
        JOIN lt.tag tg
        WHERE l.user = :user AND tg.name = :tag
        ORDER BY l.id DESC
    """)
    List<Lesson> findByUserAndTagNameWithPost(@Param("user") User user,
                                              @Param("tag") String tag);


    boolean existsLessonByUserAndPost(User user, Post post);

    Optional<Lesson> findByUserAndPost(User user, Post post);

    void deleteAllByPost(Post post);

    List<Lesson> findLessonsByPost(Post post);
}