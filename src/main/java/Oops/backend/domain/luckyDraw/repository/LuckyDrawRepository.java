package Oops.backend.domain.luckyDraw.repository;

import Oops.backend.domain.luckyDraw.entity.LuckyDraw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LuckyDrawRepository extends JpaRepository<LuckyDraw, Long> {

    /**
     * 사용자가 이전에 뽑은 3개를 제외한 부적 중 뽑기
     */
    @Query("""
    SELECT ld FROM LuckyDraw ld
    WHERE ld NOT IN :lastLuckyDraws
    ORDER BY function('RAND')
    """)
    Page<LuckyDraw> findRandomExcludingList(@Param("lastLuckyDraws") List<LuckyDraw> lastLuckyDraws, Pageable pageable);

    /**
     * 사용자가 이전에 뽑은 부적이 없는 경우 10개 중에 뽑기
     */
    @Query("""
    SELECT ld FROM LuckyDraw ld
    ORDER BY function('RAND')
""")
    Page<LuckyDraw> findRandomLuckyDraw(Pageable pageable);
}
