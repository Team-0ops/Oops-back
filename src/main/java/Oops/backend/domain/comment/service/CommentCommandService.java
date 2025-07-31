package Oops.backend.domain.comment.service;

import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.user.entity.User;

public interface CommentCommandService {

    Comment leaveComment(Long postId, User user, CommentRequestDto.LeaveCommentDto request);
    void deleteComment(Long postId, Long commentId, User user);
}
