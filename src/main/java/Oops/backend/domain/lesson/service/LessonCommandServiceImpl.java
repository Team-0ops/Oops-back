package Oops.backend.domain.lesson.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.auth.repository.AuthRepository;
import Oops.backend.domain.lesson.dto.request.CreateLessonRequest;
import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.lesson.entity.LessonTag;
import Oops.backend.domain.lesson.repository.LessonRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.tag.entity.Tag;
import Oops.backend.domain.tag.service.TagCommandService;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.entity.UserTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonCommandServiceImpl implements LessonCommandService{

    private final LessonRepository lessonRepository;
    private final PostQueryService postQueryService;
    private final TagCommandService tagCommandService;
    private final LessonTagCommandService lessonTagCommandService;
    private final AuthRepository authRepository;

    @Override
    @Transactional
    public Lesson createLesson(User user, Long postId, CreateLessonRequest request) {

        Post post = postQueryService.findPost(postId);
        Optional<User> user1 = authRepository.findById(user.getId());
        // 게시글 작성자는 교훈을 작성할 수 없음
        log.info("게시글 작성자와 비교");
        if (user.getId() == post.getUser().getId()){
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "게시글 작성자는 교훈을 작성할 수 없습니다.");
        }

        // 교훈은 한 번밖에 작성할 수 없음 -> 이미 작성했다면 오류
        log.info("이미 작성했는지 비교");
        if (lessonRepository.existsLessonByUserAndPost(user, post)){
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "해당 게시글에 작성된 교훈이 이미 존재합니다.");
        }

        String title = request.getTitle();
        String content = request.getContent();
        
        // 태그들을 가져오고, 없다면 생성해서 가져온다.
        LinkedHashSet<Tag> tags = tagCommandService.findOrCreateTagsByNames(request.getTags());

        Lesson newLesson = Lesson.of(title, content, user1.get(), post);

        // 추후 Lesson과 Tag가 서로 참조할 수 있도록 LessonTag도 생성한다.
        LinkedHashSet<LessonTag> lessonTags = tags.stream()
                .map(lessonTagCommandService::createLessonTag)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // 양방향 연관 관계 연결
        lessonTags.forEach(newLesson::addLessonTag);

        // User가 태그를 통해 교훈을 참조할 수 있도록 UserTag 테이블에도 데이터를 추가한다.
        LinkedHashSet<UserTag> userTags = tags.stream()
                .map(UserTag::of)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // 양방향 연관 관계 연결
        userTags.forEach(user1.get()::addUserTag);

        log.info("저장");
        return lessonRepository.save(newLesson);
    }

    @Override
    public void deleteAllLessonsOfPost(Post post) {
        lessonRepository.deleteAllByPost(post);
    }


}
