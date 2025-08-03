package Oops.backend.domain.comment.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.repository.CommentRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService{

    private final CommentRepository commentRepository;
    private final PostQueryService postQueryService;
    private final CommentLikeService commentLikeService;

    @Override
    public Comment leaveComment(Long postId, User user, CommentRequestDto.LeaveCommentDto request) {

        Post post = postQueryService.findPost(postId);

        String content = request.getContent();

        Comment newComment = Comment.of(post, user, content);

        //대댓글이라면
        if (request.getParentId() != null){

            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(()-> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

            parentComment.addReplyComment(newComment);

        }

        return commentRepository.save(newComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        Post post = postQueryService.findPost(postId);

        // 게시글 작성한 사용자가 아니거나 댓글을 단 사용자가 아닐 경우 오류
        if (post.getUser().equals(user) || comment.getUser().equals(user)){
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void cheerComment(Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        if(commentLikeService.findCommentLike(comment, user).isEmpty()){
            comment.plusCheer();
            commentLikeService.createCommentLike(comment, user);
        }
        else{
            comment.minusCheer();
            commentLikeService.deleteCommentLike(comment, user);
        }

    }
}
