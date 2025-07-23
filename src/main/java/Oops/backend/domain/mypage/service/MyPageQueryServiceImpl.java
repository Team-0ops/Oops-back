package Oops.backend.domain.mypage.service;

import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.lesson.repository.LessonRepository;
import Oops.backend.domain.mypage.dto.response.MyLessonResponseDto;
import Oops.backend.domain.mypage.dto.response.MyPostResponseDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.user.entity.User;
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
}
