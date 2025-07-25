package Oops.backend.domain.post.repository;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.entity.PostLike;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsPostLikeByPostAndUser(Post post, User user);
    PostLike findByPostAndUser(Post post, User user);
}