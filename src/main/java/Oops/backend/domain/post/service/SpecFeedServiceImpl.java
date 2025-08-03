package Oops.backend.domain.post.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.category.repository.UserAndCategoryRepository;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.repository.SpecFeedRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecFeedServiceImpl implements SpecFeedService {

    private final SpecFeedRepository specFeedRepository;
    private final UserAndCategoryRepository userAndCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final RandomTopicRepository randomTopicRepository;

    /**
     * 베스트 게시글 전체 조회
     */
    @Override
    @Transactional
    public PostResponse.PostPreviewListDto getBestPostList(LocalDateTime cutoff, Pageable pageable){
        Page<Post> posts = specFeedRepository.sortByBestPost(cutoff, pageable);
        if (posts.getContent().isEmpty()) {
            throw new GeneralException(ErrorStatus.NO_POST);
        }

        return toPreviewListDto(posts, "베스트 Failers");
    }

    /**
     * 즐겨찾기한 카테고리 게시글 전체 조회
     */
    @Override
    @Transactional
    public PostResponse.PostPreviewListDto getMarkedPostList(Situation situation, LocalDateTime cutoff, Pageable pageable, User user){
        // 사용자가 즐겨찾기한 카테고리 아이디 조회
        List<Long> userCategoryIds = userAndCategoryRepository.findCategoryIdsByUser(user);
        if (userCategoryIds.isEmpty()){
            throw new GeneralException(ErrorStatus.NO_BOOKMARKED);
        }

        // 즐찾 카테고리의 게시글 최신순 정렬하여 allPosts에 저장
        Page<Post> posts = specFeedRepository.findByCategoryIdInAndSituationAndCreatedAtBefore(userCategoryIds, situation, cutoff, pageable);

        return toPreviewListDto(posts, "즐겨찾기한 실패담");
    }

    /**
     * 카테고리별 피드
     */
    @Override
    @Transactional
    public PostResponse.PostPreviewListDto getPostByCategoryList(Situation situation, LocalDateTime cutoff, Pageable pageable, Long categoryId){

        String categoryName = categoryRepository.findNameById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));

        Page<Post> posts = specFeedRepository.findByCategoryIdAndSituationAndCreatedAtBeforeWithCategory(categoryId, situation, cutoff, pageable);

        return toPreviewListDto(posts, categoryName + " 카테고리");
    }

    /**
     * 이번주 랜덤 주제 피드
     */
    @Override
    @Transactional
    public PostResponse.PostPreviewListDto getThisWeekPostList(Situation situation, LocalDateTime cutoff, Pageable pageable, Long  topicId){
        String topicName = randomTopicRepository.findNameById(topicId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TOPIC_NOT_FOUND));

        Page<Post> posts = specFeedRepository.findByTopicIdAndSituationAndCreatedAtBefore(topicId, situation, cutoff, pageable);

        return toPreviewListDto(posts, topicName);
    }

    /**
     * 저번주 랜덤 주제 피드
     */
    @Override
    @Transactional
    public List<PostResponse.PostPreviewListDto> getLastWeekPostList(Situation situation, LocalDateTime cutoff, Pageable pageable, Long  topicId){
        List<PostResponse.PostPreviewListDto> result = new ArrayList<>();

        String topicName = randomTopicRepository.findNameById(topicId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TOPIC_NOT_FOUND));

        // top 3 실패담 조회
        Page<Post> bestPosts = specFeedRepository.findTop3BestPostsByTopic(topicId, cutoff, PageRequest.of(0, 3));
        if (bestPosts.isEmpty()){
            throw new GeneralException(ErrorStatus.NO_POST, "TOP 3 실패담이 없습니다.");
        }
        PostResponse.PostPreviewListDto bestPostDto = toPreviewListDto(bestPosts, "최고의 " + topicName + " 실패담 top 3");
        result.add(bestPostDto);

        // top 3 실패담의 아이디 수집
        List<Long> bestPostIds = bestPosts.stream()
                .map(Post::getId)
                .toList();

        // 나머지 게시글 조회 (top 3 제외)
        Page<Post> posts = specFeedRepository.findFilteredPostsExcludingIds(
                topicId, situation, cutoff, bestPostIds, pageable);

        // posts가 없으면 예외
        if (posts.isEmpty()) {
            throw new GeneralException(ErrorStatus.NO_POST, "TOP 3을 제외한 실패담이 없습니다.");
        }

        // posts 추가
        PostResponse.PostPreviewListDto postDto = toPreviewListDto(posts, "조회수 순 " + topicName + " 실패담");
        result.add(postDto);

        return result;
    }

    /**
     * 결과 DTO 변환 메서드
     */
    private PostResponse.PostPreviewListDto toPreviewListDto(Page<Post> posts, String listName){

        if (posts == null) {
            return PostResponse.PostPreviewListDto.builder()
                    .name(listName)
                    .posts(Collections.emptyList())
                    .isLast(true) // null일 경우 더 이상 페이지 없음으로 처리
                    .build();
        }

        List<PostResponse.PostPreviewDto> previews = posts.getContent().stream()
                .map(PostResponse.PostPreviewDto::from)
                .collect(Collectors.toList());

        return PostResponse.PostPreviewListDto.builder()
                .name(listName)
                .posts(previews)
                .isLast(posts.isLast())
                .build();
    }
}
