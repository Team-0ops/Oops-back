package Oops.backend.domain.comment.service;

import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.entity.CommentLike;
import Oops.backend.domain.comment.repository.CommentLikeRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    public Optional<CommentLike> findCommentLike(Comment comment, User user){

        return commentLikeRepository.findCommentLikeByCommentAndUser(comment, user);
    }

    public boolean existsCommentLike(Comment comment, User user){

        return commentLikeRepository.existsCommentLikeByCommentAndUser(comment, user);
    }

    @Transactional
    public void createCommentLike(Comment comment, User user){

        CommentLike newCommentLike = CommentLike.of(user, comment);

        commentLikeRepository.save(newCommentLike);

        log.info("commentLikeRepository.save 호출 완료");
    }

    public void deleteCommentLike(Comment comment, User user){

        Optional<CommentLike> commentLike = commentLikeRepository.findCommentLikeByCommentAndUser(comment, user);

        commentLike.ifPresent(commentLikeRepository::delete);
    }

}
