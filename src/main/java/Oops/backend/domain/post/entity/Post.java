package Oops.backend.domain.post.entity;

import Oops.backend.domain.post.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    private int likes;

    private int watching;

    private int report;

    @Enumerated(EnumType.STRING)
    private Situation situation;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "post_comment_type", joinColumns = @JoinColumn(name = "post_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private List<CommentType> allowedCommentTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime createdAt;

    // 연관관계
    private Long userId;
    private Long topicId;
    private Long previousPostId;



    // 편의 메서드
    public void increaseViewCount() {
        this.watching++;
    }

    public void increaseReportCount() {
        this.report++;
    }
}