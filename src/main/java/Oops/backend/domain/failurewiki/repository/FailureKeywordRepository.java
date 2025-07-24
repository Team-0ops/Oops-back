package Oops.backend.domain.failurewiki.repository;

import Oops.backend.domain.failurewiki.entity.FailureKeyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FailureKeywordRepository extends JpaRepository<FailureKeyword, Long> {

    @Query("SELECT k FROM FailureKeyword k ORDER BY function('RAND')")
    List<FailureKeyword> findRandom(Pageable pageable);

    Optional<FailureKeyword> findByKeyword(String keyword);

}
