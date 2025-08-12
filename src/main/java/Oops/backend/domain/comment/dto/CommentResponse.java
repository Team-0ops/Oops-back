package Oops.backend.domain.comment.dto;

import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class CommentResponse {

    Long commentId;

    String content;

    Long userId;

    String userName;

    String imageUrl;

    Integer likes;

    Long parentId;

    LocalDateTime createdAt;

    Boolean liked;

    @Builder
    private CommentResponse(Long commentId,
                            String content,
                            Long userId,
                            String userName,
                            String imageUrl,
                            Integer likes,
                            LocalDateTime createdAt,
                            Long parentId,
                            Boolean liked){

        this.commentId = commentId;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.likes = likes;
        this.createdAt = createdAt;
        this.parentId = parentId;
        this.liked = liked;
    }

    public static CommentResponse of(Comment comment, Boolean liked){

        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getUserName())
                .content(comment.getContent())
                .imageUrl(comment.getUser().getProfileImageUrl())
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .parentId(Optional.ofNullable(comment.getParent()).map(Comment::getId).orElse(null))
                .liked(liked)
                .build();
    }
}
