package Oops.backend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "refresh_token",
        uniqueConstraints = @UniqueConstraint(name = "ux_refresh_token_user", columnNames = "user_id")
)
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(nullable=false, length=512)
    private String token;

    public static RefreshToken of(Long userId, String token) {
        RefreshToken rt = new RefreshToken();
        rt.userId = userId;
        rt.token = token;
        return rt;
    }

    public void setToken(String token) { this.token = token; }
}
