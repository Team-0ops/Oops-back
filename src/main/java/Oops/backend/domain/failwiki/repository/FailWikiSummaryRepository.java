package Oops.backend.domain.failwiki.repository;

import Oops.backend.domain.failwiki.entity.FailWikiSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FailWikiSummaryRepository extends JpaRepository<FailWikiSummary, Long> {
    Optional<FailWikiSummary> findByKeyword(String keyword);
}