package Oops.backend.domain.user.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.tag.entity.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserTag extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    private UserTag(User user, Tag tag){
        this.user = user;
        this.tag = tag;
    }

    public static UserTag of(Tag tag){
        return UserTag.builder()
                .tag(tag)
                .build();
    }



}
