package Oops.backend.domain.auth.entity;


import Oops.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "social_accounts",
        uniqueConstraints = @UniqueConstraint(name="ux_social_provider",
                columnNames={"provider","providerId"}))
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="user_id")
    private User user;

    @Column(nullable=false, length=32)
    private String provider;

    @Column(nullable=false, length=128)
    private String providerId;

    @Column(length=255)
    private String emailFromProvider;
}
