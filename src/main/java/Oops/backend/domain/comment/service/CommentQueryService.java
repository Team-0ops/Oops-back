package Oops.backend.domain.comment.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.comment.dto.CommentResponse;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.repository.CommentRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final PostQueryService postQueryService;

    public Comment findComment(Long commentId){

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        return comment;
    }

    public List<CommentResponse> findCommentsOfPost(Long postId, User user){

        Post post = postQueryService.findPost(postId);

        List<CommentResponse> Comments = post.getComments().stream()
                .map(CommentResponse::from)
                .toList();

        return Comments;
    }


}
