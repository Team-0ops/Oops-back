package Oops.backend.domain.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class CommentRequestDto {

    @Getter
    @Builder
    public static class LeaveCommentDto {

        @NotNull
        String content;

    }

}
