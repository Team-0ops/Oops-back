package Oops.backend.domain.comment.service;

import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.user.entity.User;

public interface CommentCommandService {

    void leaveComment(Long postId, User user, CommentRequestDto.LeaveCommentDto request);
}
