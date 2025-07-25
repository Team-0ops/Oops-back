package Oops.backend.domain.post.repository;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 사용자가 작성한 전체 글 조회
    List<Post> findByUser(User user);

    // 특정 사용자가 특정 카테고리에 작성한 글 조회
    List<Post> findByUserAndCategory(User user, Category category);

}
