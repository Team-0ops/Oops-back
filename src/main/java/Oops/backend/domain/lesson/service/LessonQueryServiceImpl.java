package Oops.backend.domain.lesson.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.lesson.dto.response.GetLessonResponse;
import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.lesson.repository.LessonRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonQueryServiceImpl implements LessonQueryService{

    private final LessonRepository lessonRepository;
    private final PostQueryService postQueryService;

    @Override
    @Transactional
    public GetLessonResponse getLesson(User user, Long postId) {

        Post post = postQueryService.findPost(postId);

        Lesson lesson = lessonRepository.findByUserAndPost(user, post)
                .orElse(null);

        if (lesson == null) return null;

        String title = lesson.getTitle();
        String content = lesson.getContent();

        Set<String> tagNames = lesson.getTags().stream()
                .map((tags) -> tags.getTag().getName())
                .collect(Collectors.toSet());

        return GetLessonResponse.of(content, title, tagNames);
    }

    @Override
    public List<Lesson> findLessonsByPost(Post post) {
        return lessonRepository.findLessonsByPost(post);
    }
}
