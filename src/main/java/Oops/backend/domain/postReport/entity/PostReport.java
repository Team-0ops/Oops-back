package Oops.backend.domain.postReport.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReport extends BaseEntity {

    private String reason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post")
    private Post post;

    @Column(nullable = false, length = 300)
    private String content;

}