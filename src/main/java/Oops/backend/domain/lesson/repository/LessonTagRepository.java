package Oops.backend.domain.lesson.repository;

import Oops.backend.domain.lesson.entity.LessonTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonTagRepository extends JpaRepository<LessonTag, Long> {
}
