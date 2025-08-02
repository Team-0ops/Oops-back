package Oops.backend.domain.user.entity;

import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
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
    @ColumnDefault("0")
    private Integer point;

    @Column
    @ColumnDefault("0")
    private Integer report;

    @Column
    @Builder.Default
    private Boolean isSelected = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserLastLuckyDraw> lastLuckyDraws = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Column
    private Set<UserTag> tags = new LinkedHashSet<>();

    public void addUserTag(UserTag userTag) {
        this.tags.add(userTag);
        userTag.setUser(this);
    }

    public void addPoint(int amount) {
        if (this.point == null) this.point = 0;
        this.point += amount;
    }

}

