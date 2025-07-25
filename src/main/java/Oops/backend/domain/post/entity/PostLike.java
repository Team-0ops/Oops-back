package Oops.backend.domain.post.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Builder
    private PostLike(Post post, User user){
        this.post = post;
        this.user = user;
    }

    public static PostLike createPostLike(Post post, User user){
        return PostLike.builder()
                .post(post)
                .user(user)
                .build();
    }
}