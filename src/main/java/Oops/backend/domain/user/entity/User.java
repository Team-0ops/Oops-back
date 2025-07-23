package Oops.backend.domain.user.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.luckyDraw.entity.LuckyDraw;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column
    private String userName;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private Integer point;

    @Column
    private Integer report;

    @OneToMany
    @Column
    private List<LuckyDraw> lastLuckyDraws;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Column
    private Set<UserTag> tags = new LinkedHashSet<>();

    public void addUserTag(UserTag userTag) {
        this.tags.add(userTag);
        userTag.setUser(this);
    }

}
