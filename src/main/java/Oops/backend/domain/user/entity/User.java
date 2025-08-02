package Oops.backend.domain.user.entity;

import Oops.backend.domain.common.BaseEntity;
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

    @Column
    @Builder.Default
    private Boolean isSelected = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserLastLuckyDraw> lastLuckyDraws = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Column
    @Builder.Default
    private List<UserTag> tags = new ArrayList<>();

}

