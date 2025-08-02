package Oops.backend.domain.comment.dto;

import Oops.backend.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class CommentResponse {

    Long commentId;

    String content;

    Long userId;

//    String imageUrl;

    Integer likes;

    Long parentId;

    LocalDateTime createdAt;

    @Builder
    private CommentResponse(Long commentId,
                            String content,
                            Long userId,
                            String imageUrl,
                            Integer likes,
                            LocalDateTime createdAt,
                            Long parentId){

        this.commentId = commentId;
        this.content = content;
        this.userId = userId;
//        this.imageUrl = imageUrl;
        this.likes = likes;
        this.createdAt = createdAt;
        this.parentId = parentId;
    }

    public static CommentResponse from(Comment comment){

        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
//                .imageUrl(comment.getUser().getImageUrl())
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .parentId(Optional.ofNullable(comment.getParent()).map(Comment::getId).orElse(null))
                .build();
    }
}
