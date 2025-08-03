package Oops.backend.domain.mypage.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.commentReport.repository.CommentReportRepository;
import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.lesson.repository.LessonRepository;
import Oops.backend.domain.mypage.dto.response.MyLessonResponseDto;
import Oops.backend.domain.mypage.dto.response.MyPostResponseDto;
import Oops.backend.domain.mypage.dto.response.MyProfileResponseDto;
import Oops.backend.domain.mypage.dto.response.OtherProfileResponseDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.postReport.repository.PostReportRepository;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageQueryServiceImpl implements MyPageQueryService {

    private final PostRepository postRepository;
    private final LessonRepository lessonRepository;
    private final CategoryRepository categoryRepository;
    private final CommentReportRepository commentReportRepository;
    private final PostReportRepository postReportRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MyPostResponseDto> getMyPosts(User user, Long categoryId) {
        List<Post> posts;

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
            posts = postRepository.findByUserAndCategory(user, category);
        } else {
            posts = postRepository.findByUser(user);
        }

        return posts.stream()
                .map(MyPostResponseDto::from)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public List<MyLessonResponseDto> getMyLessons(User user, String tag) {
        if (tag != null) {
            return lessonRepository.findByUserAndTagName(user, tag)
                    .stream()
                    .map(MyLessonResponseDto::from)
                    .toList();
        }
        return lessonRepository.findByUser(user)
                .stream()
                .map(MyLessonResponseDto::from)
                .toList();
    }
    /*
    @Override
    @Transactional(readOnly = true)
    public List<MyLessonResponseDto> getMyLessons(User user, Long categoryId) {
        List<Lesson> lessons;

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));
            lessons = lessonRepository.findByUserAndCategory(user, category);
        } else {
            lessons = lessonRepository.findByUser(user);
        }

        return lessons.stream()
                .map(MyLessonResponseDto::from)
                .toList();
    }
    */

    @Override
    @Transactional(readOnly = true)
    public MyProfileResponseDto getMyProfile(User user) {
        long commentReportCount = commentReportRepository.countByReportUser(user);
        long postReportCount = postReportRepository.countByUser(user);

        return MyProfileResponseDto.from(user, commentReportCount, postReportCount);
    }

    @Override
    @Transactional(readOnly = true)
    public OtherProfileResponseDto getOtherUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Post> posts = postRepository.findByUser(user); // 사용자 게시글 가져오기

        return OtherProfileResponseDto.from(user, posts);
    }

}
