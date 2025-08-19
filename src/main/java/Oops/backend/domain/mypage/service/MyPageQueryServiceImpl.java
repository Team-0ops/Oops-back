package Oops.backend.domain.mypage.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.config.s3.S3ImageService;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.commentReport.repository.CommentReportRepository;
import Oops.backend.domain.lesson.entity.Lesson;
import Oops.backend.domain.lesson.repository.LessonRepository;
import Oops.backend.domain.mypage.dto.response.*;
import Oops.backend.domain.post.dto.PostSummaryDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.postReport.repository.PostReportRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MyPageQueryServiceImpl implements MyPageQueryService {

    private final PostRepository postRepository;
    private final LessonRepository lessonRepository;
    private final CategoryRepository categoryRepository;
    private final CommentReportRepository commentReportRepository;
    private final PostReportRepository postReportRepository;
    private final UserRepository userRepository;
    private final RandomTopicRepository randomTopicRepository;
    private final S3ImageService s3ImageService;

    @Override
    public List<MyPostResponseDto> getMyPosts(User user, Long categoryId, Long topicId, Situation situation) {

        if (categoryId != null && topicId != null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "카테고리와 랜덤 주제를 동시에 선택할 수 없습니다.");
        }

        final List<Post> posts;

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND, "존재하지 않는 카테고리입니다."));
            posts = (situation != null)
                    ? postRepository.findByUserAndCategoryAndSituation(user, category, situation)
                    : postRepository.findByUserAndCategory(user, category);
        } else if (topicId != null) {
            RandomTopic topic = randomTopicRepository.findById(topicId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND, "존재하지 않는 랜덤 주제입니다."));
            posts = (situation != null)
                    ? postRepository.findByUserAndTopicAndSituation(user, topic, situation)
                    : postRepository.findByUserAndTopic(user, topic);
        } else {
            posts = (situation != null)
                    ? postRepository.findByUserAndSituation(user, situation)
                    : postRepository.findByUser(user);
        }

        return posts.stream()
                .map(post -> {
                    List<String> urls = List.of();
                    if (post.getImages() != null && !post.getImages().isEmpty()) {
                        urls = post.getImages().stream()
                                // 필요하면 .limit(n) 으로 썸네일만
                                .map(key -> {
                                    try { return s3ImageService.getPreSignedUrl(key); }
                                    catch (Exception e) { return null; } // 실패 시 해당 이미지만 제외
                                })
                                .filter(Objects::nonNull)
                                .toList();
                    }
                    return MyPostResponseDto.fromWithImages(post, urls);
                })
                .toList();
    }


    @Override
    public List<MyLessonResponseDto> getMyLessons(User user, String tag) {
        List<Lesson> lessons = (tag != null && !tag.isBlank())
                ? lessonRepository.findByUserAndTagNameWithPost(user, tag)
                : lessonRepository.findByUserWithPostAndTags(user);

        return lessons.stream().map(lesson -> {
            var post = lesson.getPost();
            List<String> urls = List.of();
            if (post != null && post.getImages() != null && !post.getImages().isEmpty()) {
                urls = post.getImages().stream()
                        .map(key -> {
                            try { return s3ImageService.getPreSignedUrl(key); }
                            catch (Exception e) { return null; }
                        })
                        .filter(Objects::nonNull)
                        .toList();
            }
            return MyLessonResponseDto.fromWithImages(lesson, urls);
        }).toList();
    }

    @Override
    public MyProfileResponseDto getMyProfile(User user) {
        long commentReportCount = commentReportRepository.countByReportUser(user);
        long postReportCount = postReportRepository.countByUser(user);

        String imageKey = user.getProfileImageUrl();

        String imageUrl = null;
        if (imageKey != null && !imageKey.isBlank()) {
            try {
                imageUrl = s3ImageService.getPreSignedUrl(imageKey); // 유효 5분짜리 URL
            } catch (Exception e) {
                // presigned URL 생성 실패 시, 프론트 요구에 맞춰 null로 내려 안정적으로 응답
                imageUrl = null;
                // 필요하면 로깅만 남김
                // log.warn("Failed to create presigned url for key={}", imageKey, e);
            }
        }

        return MyProfileResponseDto.from(user, commentReportCount, postReportCount, imageUrl);
    }



    @Override
    public OtherProfileResponseDto getOtherUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Post> posts = postRepository.findByUser(user);

        List<Situation> bestSituations = List.of(Situation.OOPS, Situation.OVERCOMING, Situation.OVERCOME);
        List<Post> bestFailers = postRepository.findBestFailers(bestSituations, PageRequest.of(0, 6));

        // ✅ Presigned URL 생성기를 넘겨서 DTO 변환
        List<OtherUserPostDto> postDtos = posts.stream()
                .map(p -> OtherUserPostDto.from(p, key -> {
                    try { return s3ImageService.getPreSignedUrl(key); }
                    catch (Exception e) { return null; }
                }))
                .toList();

        return OtherProfileResponseDto.from(user, postDtos, bestFailers);
    }


}
