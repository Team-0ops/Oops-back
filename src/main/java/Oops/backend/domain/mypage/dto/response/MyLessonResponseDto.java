package Oops.backend.domain.mypage.dto.response;

import Oops.backend.domain.lesson.entity.Lesson;
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

    //게시글도 추가
    private Long postId;
    private String postTitle;
    private String postContent;
    private String categoryName;  // nullable
    private String topicName;     // nullable


    public static MyLessonResponseDto from(Lesson lesson) {

        List<String> tagNames = lesson.getTags().stream()
                .map(lessonTag -> lessonTag.getTag().getName())
                .toList();

        return MyLessonResponseDto.builder()
                .lessonId(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .tags(tagNames)
                .postId(lesson.getPost().getId())
                .postTitle(lesson.getPost().getTitle())
                .postContent(lesson.getPost().getContent())
                .categoryName(lesson.getPost().getCategory() != null
                        ? lesson.getPost().getCategory().getName() : null)
                .topicName(lesson.getPost().getTopic() != null
                        ? lesson.getPost().getTopic().getName() : null)
                .build();
    }
}
