package Oops.backend.domain.comment.repository;

import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying
    @Query("update Comment c set c.likes = c.likes+1 where c.id = :commentId")
    public void plusCommentLikes(@Param("commentId") Long commentId);

    @Modifying
    @Query("update Comment c set c.likes = c.likes-1 where c.id = :commentId")
    public void minusCommentLikes(@Param("commentId") Long commentId);

    public List<Comment> findCommentByPostOrderByLikesDesc(Post post);

    public List<Comment> findCommentByPostOrderByCreatedAtDesc(Post post);

    // 일반 댓글만 좋아요순으로 조회 (좋아요가 같으면 최신순으로 정렬)
    List<Comment> findByPostAndParentIsNullOrderByLikesDescCreatedAtDesc(Post post);
    
    // 일반 댓글만 최신순으로 조회
    List<Comment> findByPostAndParentIsNullOrderByCreatedAtDesc(Post post);
    
    // 답글들을 최신순으로 조회
    List<Comment> findByPostAndParentIsNotNullOrderByCreatedAtDesc(Post post);

}