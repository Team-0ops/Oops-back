package Oops.backend.domain.post.repository;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.entity.PostLike;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsPostLikeByPostAndUser(Post post, User user);
    Optional<PostLike> findByPostAndUser(Post post, User user);
}