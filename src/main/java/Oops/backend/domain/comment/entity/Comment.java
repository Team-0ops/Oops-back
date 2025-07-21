package Oops.backend.domain.comment.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.yaml.snakeyaml.comments.CommentType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column
    private CommentType commentType;

    @Column
    private Integer likes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    private Comment(Post post, User user, String content){

        this.post = post;
        this.likes = 0;
        this.user = user;
        this.content = content;

    }


    public static Comment of(Post post, User user, String content){

        return Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();

    }

}