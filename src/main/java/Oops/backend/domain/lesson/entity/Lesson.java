package Oops.backend.domain.lesson.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Set<LessonTag> tags;

    public void addLessonTag(LessonTag lessonTag) {
        this.tags.add(lessonTag);
        lessonTag.setLesson(this);
    }

    @Builder
    private Lesson(String title, String content, User user, Post post){
        this.title = title;
        this.content = content;
        this.user = user;
        this.post = post;
        this.tags = new LinkedHashSet<>();

    }

    public static Lesson of(String title, String content, User user, Post post){

        return Lesson.builder()
                .title(title)
                .content(content)
                .user(user)
                .post(post)
                .build();
    }
}
