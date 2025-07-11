package Oops.backend.domain.lessonTag.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonTag extends BaseEntity {

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
