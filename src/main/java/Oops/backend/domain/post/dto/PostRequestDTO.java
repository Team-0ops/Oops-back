package Oops.backend.domain.post.dto;

import Oops.backend.domain.post.entity.CommentType;
import Oops.backend.domain.post.entity.Situation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class PostRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Situation situation;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long topicId;

    private Long previousPostId;

    @NotNull
    private List<CommentType> allowedCommentTypes;
}
