package Oops.backend.domain.lesson.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateLessonRequest {

    String title;

    @NotNull
    String content;

    List<String> tags;

}
