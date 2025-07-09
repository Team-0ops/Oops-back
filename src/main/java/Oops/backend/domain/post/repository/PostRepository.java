package Oops.backend.domain.post.repository;

import Oops.backend.domain.post.entity.Post;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsById(@Nullable Long postId);
}
