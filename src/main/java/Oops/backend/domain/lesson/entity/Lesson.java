package Oops.backend.domain.lesson.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson extends BaseEntity {

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<LessonTag> tags;
}