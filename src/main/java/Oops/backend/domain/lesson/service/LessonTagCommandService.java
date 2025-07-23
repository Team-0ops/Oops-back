package Oops.backend.domain.lesson.service;

import Oops.backend.domain.lesson.entity.LessonTag;
import Oops.backend.domain.tag.entity.Tag;

public interface LessonTagCommandService {

    LessonTag createLessonTag(Tag tag);

}
