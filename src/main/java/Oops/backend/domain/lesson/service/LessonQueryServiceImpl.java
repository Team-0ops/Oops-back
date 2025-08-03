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
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST, "교훈이 존재하지 않습니다."));
        
        /* TODO : 검증 로직 수정
        if (user.getId() != lesson.getUser().getId()){
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "다른 사용자의 교훈은 조회할 수 없습니다.");
        }
        */

        String title = lesson.getTitle();
        String content = lesson.getContent();

        Set<String> tagNames = lesson.getTags().stream()
                .map((tags) -> tags.getTag().getName())
                .collect(Collectors.toSet());

        return GetLessonResponse.of(content, title, tagNames);
    }
}
