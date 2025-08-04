package Oops.backend.domain.post.repository;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 사용자가 작성한 전체 글 조회
    List<Post> findByUser(User user);

    // 특정 사용자가 특정 카테고리에 작성한 글 조회
    List<Post> findByUserAndCategory(User user, Category category);

    // 같은 카테고리, 자신 제외, 최신순 6개
    List<Post> findTop6ByCategoryIdAndIdNotOrderByCreatedAtDesc(Long categoryId, Long excludePostId);

    // 같은 랜덤 주제 기반 최신글 6개
    List<Post> findTop6ByTopicIdAndIdNotOrderByCreatedAtDesc(Long topicId, Long excludePostId);

    // 베스트 실패담 5개 (좋아요 순)
    @Query("SELECT p FROM Post p WHERE p.situation IN :situations ORDER BY p.likes DESC")
    List<Post> findBestFailers(@Param("situations") List<Situation> situations, Pageable pageable);

    // PostGroup에서 Post 찾기
    Optional<Post> findPostByPostGroupAndSituation(PostGroup postGroup, Situation situation);

    //situation
    List<Post> findByUserAndSituation(User user, Situation situation);
}

