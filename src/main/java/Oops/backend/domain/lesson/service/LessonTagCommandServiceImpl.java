package Oops.backend.domain.lesson.service;

import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.lesson.entity.LessonTag;
import Oops.backend.domain.lesson.repository.LessonTagRepository;
import Oops.backend.domain.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonTagCommandServiceImpl implements LessonTagCommandService{

    private final LessonTagRepository lessonTagRepository;

    @Override
    public LessonTag createLessonTag(Tag tag) {

        LessonTag newLessonTag = LessonTag.of(tag);

        return newLessonTag;
    }
}
