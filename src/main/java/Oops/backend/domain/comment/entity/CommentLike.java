package Oops.backend.domain.comment.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Builder
    private CommentLike(User user, Comment comment){
        this.user = user;
        this.comment = comment;
    }

    public static CommentLike of(User user, Comment comment){

        return CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();

    }



}
