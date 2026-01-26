package Oops.backend.domain.post.repository;

import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.entity.PostLike;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsPostLikeByPostAndUser(Post post, User user);
    Optional<PostLike> findByPostAndUser(Post post, User user);

    @Query("select pl.post.id from PostLike pl where pl.user = :user and pl.post.id in :postIds")
    List<Long> findLikedPostIds(@Param("user") User user, @Param("postIds") List<Long> postIds);
}