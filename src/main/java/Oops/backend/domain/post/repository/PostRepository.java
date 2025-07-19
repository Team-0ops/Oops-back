package Oops.backend.domain.post.repository;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.category.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 베스트 실패담 5개 조회
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.comments c " +
            "WHERE p.createdAt <= :cutoff " +
            "GROUP BY p " +
            "ORDER BY (p.watching + 5 * p.likes + 10 * COUNT(c)) DESC")
    List<Post> findTopBestPostBefore(@Param("cutoff") LocalDateTime cutoff, Pageable pageable);

    /**
     * 즐찾 카테고리 중 최신 실패담 10개 조회
     */
    @Query("SELECT p FROM Post p WHERE p.category.id IN :categoryIds ORDER BY p.createdAt DESC")
    List<Post> findTop10ByCategoryIdsOrderByCreatedAtDesc(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    /**
     * 각 카테고리별 최신글 1개씩 조회
     */
    @Query(value = """
    SELECT p.* FROM post p
    INNER JOIN (
        SELECT category, MAX(created_at) AS max_created
        FROM post
        GROUP BY category
    ) latest ON p.category = latest.category AND p.created_at = latest.max_created
    """, nativeQuery = true)
    List<Post> findLatestPostPerCategory();

    /**
     * 저번 주 랜덤 주제에 대한 게시글 조회
     */
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.comments c " +
            "WHERE p.topic.id = :topicId AND p.createdAt <= :cutoff " +
            "GROUP BY p " +
            "ORDER BY (p.watching + 5 * p.likes + 10 * COUNT(c)) DESC")
    List<Post> findTop3BestPostsByTopic(@Param("topicId") Long topicId,
                                        @Param("cutoff") LocalDateTime cutoff,
                                        Pageable pageable);

    /**
     * 제목 또는 본문에 해당 키워드가 포함된 게시글 조회
     */
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Post> findByKeyword(@Param("keyword") String keyword);

    /**
     * 특정 카테고리 리스트에 포함된 게시글
     */
    List<Post> findByCategoryIn(List<Category> categories);
}
