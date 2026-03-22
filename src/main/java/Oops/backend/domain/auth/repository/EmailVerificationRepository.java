package Oops.backend.domain.auth.repository;

import Oops.backend.domain.auth.entity.EmailVerification;
import Oops.backend.domain.auth.entity.VerificationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, VerificationPurpose purpose);

    Optional<EmailVerification> findByEmailAndPurposeAndVerificationToken(String email, VerificationPurpose purpose, String token);
}
