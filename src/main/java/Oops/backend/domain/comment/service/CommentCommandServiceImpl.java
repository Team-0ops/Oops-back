package Oops.backend.domain.comment.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.entity.CommentLike;
import Oops.backend.domain.comment.repository.CommentRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService{

    private final CommentRepository commentRepository;
    private final PostQueryService postQueryService;
    private final CommentLikeService commentLikeService;
    private final UserRepository userRepository;

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

        User user1 = userRepository.findById(user.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        Post post = postQueryService.findPost(postId);

        // 게시글 작성한 사용자가 아니거나 댓글을 단 사용자가 아닐 경우 오류
        if (!comment.getUser().equals(user1)){
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "댓글 삭제 권한이 없습니다.");
        }

        commentLikeService.deleteCommentLike(comment, user);

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void cheerComment(Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        if(commentLikeService.findCommentLike(comment, user).isEmpty()){
            commentRepository.plusCommentLikes(commentId);
            commentLikeService.createCommentLike(comment, user);
        }
        else{
            try {
                commentRepository.minusCommentLikes(commentId);
                commentLikeService.deleteCommentLike(comment, user);
            } catch(DataIntegrityViolationException e){
                throw new GeneralException(ErrorStatus.ALREADY_LIKED_COMMENT);
            }
        }

    }

}
