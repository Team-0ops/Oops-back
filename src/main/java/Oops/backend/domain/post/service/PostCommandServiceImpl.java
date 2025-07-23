package Oops.backend.domain.post.service;


import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.lesson.repository.LessonRepository;
import Oops.backend.domain.lesson.service.LessonCommandService;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommandServiceImpl implements PostCommandService{

    private final PostRepository postRepository;
    private final PostLikeQueryService postLikeQueryService;
    private final PostLikeCommandService postLikeCommandService;
    private final LessonCommandService lessonCommandService;

    @Override
    @Transactional
    public void cheerPost(Long postId, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST, "존재하지 않는 게시글입니다."));

        if (!postLikeQueryService.findPostLike(user, post)){
            post.plusCheer();
            postLikeCommandService.createPostLike(post, user);
        }
        else{
            post.minusCheer();
            postLikeCommandService.deletePostLike(post, user);
        }
    }

    @Override
    @Transactional
    public void deletePost(Long postId, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST, "존재하지 않는 게시글입니다."));

        log.info("post.getUser().equals(user)={}", post.getUser().equals(user));

        // TODO : 검증 로직
        // 사용자가 게시글을 작성한 사용자와 일치하지 않을 경우
        if ((post.getUser().getId()) != user.getId()){
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "게시글을 삭제할 권한이 없습니다.");
        }

        //게시글에 대한 모든 Lesson도 제거
        lessonCommandService.deleteAllLessonsOfPost(post);

        postRepository.delete(post);
    }
}
