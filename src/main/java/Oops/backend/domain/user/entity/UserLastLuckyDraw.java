package Oops.backend.domain.user.entity;

import Oops.backend.domain.luckyDraw.entity.LuckyDraw;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLastLuckyDraw {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "lucky_draw_id")
    private LuckyDraw luckyDraw;
}