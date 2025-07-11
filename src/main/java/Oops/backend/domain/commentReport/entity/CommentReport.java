package Oops.backend.domain.commentReport.entity;

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
public class CommentReport extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter")
    private User reportUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target")
    private Comment comment;

    @Column
    private String reason;
}
