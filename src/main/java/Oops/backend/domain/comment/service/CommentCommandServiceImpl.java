package Oops.backend.domain.comment.service;

import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.repository.CommentRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService{

    private final CommentRepository commentRepository;
    private final PostQueryService postQueryService;

    @Override
    public void leaveComment(Long postId, User user, CommentRequestDto.LeaveCommentDto request) {

        Post post = postQueryService.findPost(postId);

        String content = request.getContent();

        Comment newComment = Comment.of(post, user, content);

        commentRepository.save(newComment);
    }
}
