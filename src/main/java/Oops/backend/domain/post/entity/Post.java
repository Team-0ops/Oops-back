package Oops.backend.domain.post.entity;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    @Column
    private String title;

    @Column
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private Situation situation;

    @Column(nullable = false)
    private Integer likes;

    @Column
    private Integer watching;

    @Column
    private Integer reportCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic")
    private RandomTopic topic;

    @OneToMany
    private List<Comment> comments;

    @ElementCollection
    private List<String> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private PostGroup postGroup; // PostGroup이 있다면 ManyToOne 처리 가능

    public void plusCheer(){
        this.likes++;
    }

    public void minusCheer(){
        this.likes--;
    }

}