package Oops.backend.domain.lesson.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
public class GetLessonResponse {

    String title;
    String content;
    Set<String> tagNames;

    @Builder
    public GetLessonResponse(String title, String content, Set<String> tagNames){

        this.title = title;
        this.content = content;
        this.tagNames = tagNames;

    }

    public static GetLessonResponse of(String content, String title, Set<String> tagNames){

        return GetLessonResponse.builder()
                .content(content)
                .title(title)
                .tagNames(tagNames)
                .build();
    }

}
