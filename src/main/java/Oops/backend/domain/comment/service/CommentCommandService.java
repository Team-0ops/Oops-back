package Oops.backend.domain.comment.service;

import Oops.backend.domain.comment.dto.CommentRequestDto;

public interface CommentCommandService {

    void leaveComment(Long postId, Long userId, CommentRequestDto.leaveCommentDto request);
}
