package Oops.backend.domain.comment.repository;

import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.entity.CommentLike;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findCommentLikeByCommentAndUser(Comment comment, User user);

}
