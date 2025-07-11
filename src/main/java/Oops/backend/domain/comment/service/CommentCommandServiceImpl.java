package Oops.backend.domain.comment.service;

import Oops.backend.domain.comment.dto.CommentRequestDto;
import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.repository.CommentRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService{

    private final CommentRepository commentRepository;
    private final PostQueryService postQueryService;
    private final UserQueryService userQueryService;

    @Override
    public void leaveComment(Long postId, Long userId, CommentRequestDto.leaveCommentDto request) {

        Post post = postQueryService.findPost(postId);

        User user = userQueryService.findUser(userId);

        String content = request.getContent();

        Comment newComment = Comment.of(post, user, content);

        commentRepository.save(newComment);
    }
}
