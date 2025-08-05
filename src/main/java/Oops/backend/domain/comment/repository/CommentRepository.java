package Oops.backend.domain.comment.repository;

import Oops.backend.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying
    @Query("update Comment c set c.likes = c.likes+1 where c.id = :commentId")
    public void plusCommentLikes(@Param("commentId") Long commentId);

    @Modifying
    @Query("update Comment c set c.likes = c.likes-1 where c.id = :commentId")
    public void minusCommentLikes(@Param("commentId") Long commentId);


}