package Oops.backend.domain.post.dto;

import Oops.backend.domain.comment.model.CommentType;
import Oops.backend.domain.post.model.Situation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {
    private String title;
    private String content;
    private Situation situation; // OOPS, OVERCOMING, OVERCOME
    private Long categoryId;
    private Long topicId;
    private Long previousPostId; // OOPS나 OVERCOMING 실패담의 ID
    private List<String> imageUrls;
    private List<CommentType> wantedCommentTypes;
}
