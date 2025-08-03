package Oops.backend.domain.lesson.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonTag extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    @Setter
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    private LessonTag(Lesson lesson, Tag tag){
        this.lesson = lesson;
        this.tag = tag;
    }

    public static LessonTag of(Tag tag){
        return LessonTag.builder()
                .tag(tag)
                .build();
    }

}