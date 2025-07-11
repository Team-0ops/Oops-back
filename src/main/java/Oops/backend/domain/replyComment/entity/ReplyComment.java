package Oops.backend.domain.replyComment.entity;

import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyComment extends BaseEntity {

    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    private Integer likes;

    private String content;

    @ManyToOne
    private Comment comment;
}