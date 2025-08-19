package Oops.backend.domain.mypage.dto.response;

import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class MyLessonResponseDto {
    private Long lessonId;
    private String title;
    private String content;
    private List<String> tags;

    // 게시글 정보
    private Long postId;         // 삭제 시 null
    private String postTitle;    // 삭제 시 null
    private String postContent;  // 삭제 시 null
    private String categoryName; // 삭제 시 null
    private String topicName;    // 삭제 시 null

    private List<String> postImageUrls;
    // 추가: 게시글 상태 (ACTIVE | DELETED)
    private String postStatus;

    public static MyLessonResponseDto fromWithImages(Lesson lesson, List<String> imageUrls) {
        var tags = lesson.getTags().stream()
                .map(lt -> lt.getTag().getName())
                .toList();

        var post = lesson.getPost();
        boolean deleted = (post == null);

        return MyLessonResponseDto.builder()
                .lessonId(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .tags(tags)
                .postId(deleted ? null : post.getId())
                .postTitle(deleted ? null : post.getTitle())
                .postContent(deleted ? null : post.getContent())
                .categoryName(!deleted && post.getCategory()!=null ? post.getCategory().getName() : null)
                .topicName(!deleted && post.getTopic()!=null ? post.getTopic().getName() : null)
                .postImageUrls(imageUrls == null ? List.of() : imageUrls)
                .postStatus(deleted ? "DELETED" : "ACTIVE")
                .build();
    }
}

