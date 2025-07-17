package Oops.backend.domain.user.entity;

import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.luckyDraw.entity.LuckyDraw;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
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
}
