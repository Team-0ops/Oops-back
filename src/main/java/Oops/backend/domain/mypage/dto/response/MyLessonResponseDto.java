package Oops.backend.domain.mypage.dto.response;

import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.lessonTag.entity.LessonTag;
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

    public static MyLessonResponseDto from(Lesson lesson) {
        List<String> tagNames = lesson.getLessonTags().stream()
                .map(LessonTag::getName)
                .toList();

        return MyLessonResponseDto.builder()
                .lessonId(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .tags(tagNames)
                .build();
    }
}
