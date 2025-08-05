package Oops.backend.domain.commentReport.entity;

import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReport extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter")
    private User reportUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target")
    private Comment comment;

    @Column(nullable = false, length = 300)
    private String content;

    @Builder
    private CommentReport(User reportUser, Comment comment, String content){
        this.reportUser = reportUser;
        this.comment = comment;
        this.content = content;
    }

    public static CommentReport of(User reportUser, Comment comment, String content){
        return CommentReport.builder()
                .reportUser(reportUser)
                .comment(comment)
                .content(content)
                .build();
    }

}
