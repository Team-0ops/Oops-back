package Oops.backend.domain.user.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.luckyDraw.entity.LuckyDraw;
import Oops.backend.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLastLuckyDraw> lastLuckyDraws;

    @OneToMany(cascade = CascadeType.ALL)
    @Column
    private List<Tag> tags;
}