package Oops.backend.domain.terms.repository;

import Oops.backend.domain.terms.entity.UserAndTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAndTermsRepository extends JpaRepository<UserAndTerms, Long> {
}
