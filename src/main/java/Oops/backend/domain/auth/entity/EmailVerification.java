package Oops.backend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "email_verification",
        indexes = {
                @Index(name="ix_email_purpose_created", columnList = "email,purpose,createdAt")
        })
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=320)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private VerificationPurpose purpose;

    @Column(nullable=false, length=100)
    private String codeHash;

    @Column(nullable=false)
    private LocalDateTime expiresAt;

    private LocalDateTime verifiedAt;

    @Column(nullable=false)
    private int attemptCount;

    @Column(length=64)
    private String verificationToken;

    private LocalDateTime tokenExpiresAt;

    @Column(updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isCodeExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isTokenExpired() {
        return tokenExpiresAt == null || LocalDateTime.now().isAfter(tokenExpiresAt);
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }
}
