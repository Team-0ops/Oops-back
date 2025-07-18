package Oops.backend.domain.mypage.service;

import Oops.backend.domain.auth.AuthenticatedUser;
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

    @Override
    @Transactional(readOnly = true)
    public List<MyPostResponseDto> getMyPosts(User user, Long categoryId) {
        System.out.println("user = " + user);

        List<Post> posts = (categoryId != null) ?
                postRepository.findByUserIdAndCategoryId(user.getId(), categoryId) :
                postRepository.findByUserId(user);

        for (Post post : posts) {
            System.out.println(post);
        }
        return posts.stream().map(MyPostResponseDto::from).toList();
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
