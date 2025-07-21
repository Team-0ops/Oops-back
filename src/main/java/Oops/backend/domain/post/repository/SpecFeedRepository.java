package Oops.backend.domain.post.repository;

import Oops.backend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpecFeedRepository extends JpaRepository<Post, Long> {

    /**
     * 베스트 피드 : 전체 게시글 베스트 지표 기준으로 정렬하여 조회
     */
    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN p.comments c " +
            "WHERE p.createdAt <= :cutoff " +
            "GROUP BY p " +
            "ORDER BY (p.watching + 5 * p.likes + 10 * COUNT(c)) DESC, p.createdAt DESC",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Post p WHERE p.createdAt <= :cutoff")
    Page<Post> sortByBestPost(@Param("cutoff") LocalDateTime cutoff, Pageable pageable);

    /**
     * 즐겨찾기 피드 : 사용자가 즐겨찾기한 카테고리의 게시글 최신순으로 정렬하여 조회
     */
    Page<Post> findByCategoryIdInAndCreatedAtBefore(List<Long> categoryIds, LocalDateTime cutoff, Pageable pageable);

    /**
     * 카테고리별 피드 : 특정 카테고리 아이디에 해당하는 게시글 최신순 정렬하여 조회
     */
    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.category
        WHERE p.category.id = :categoryId AND p.createdAt < :cutoff
        """)
    Page<Post> findByCategoryIdAndCreatedAtBeforeWithCategory(@Param("categoryId") Long categoryId, @Param("cutoff") LocalDateTime cutoff, Pageable pageable);

    /**
     * 저번 주 랜덤 주제 top 3 게시글 조회
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.comments c " +
            "WHERE p.topic.id = :topicId AND p.createdAt <= :cutoff " +
            "GROUP BY p " +
            "ORDER BY (p.watching + 5 * p.likes + 10 * COUNT(c)) DESC")
    List<Post> findTop3BestPostsByTopic(@Param("topicId") Long topicId,
                                        @Param("cutoff") LocalDateTime cutoff,
                                        Pageable pageable);
}
