package Oops.backend.domain.post.entity;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.model.CommentType;
import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private Situation situation;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer likes;

    @Column
    @ColumnDefault("0")
    private Integer watching;

    @Column
    @ColumnDefault("0")
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @ElementCollection
    private List<String> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private PostGroup postGroup; // PostGroup이 있다면 ManyToOne 처리 가능

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "post_comment_type", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "comment_type")
    private List<CommentType> wantedCommentTypes;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_post_id")
    private Post previousPost;
    */

    public void plusCheer(){
        this.likes++;
    }

    public void minusCheer(){
        this.likes--;
    }

}