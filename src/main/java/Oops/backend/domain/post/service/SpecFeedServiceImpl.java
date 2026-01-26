package Oops.backend.domain.post.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.config.s3.S3ImageService;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.category.repository.UserAndCategoryRepository;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.entity.SortType;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.repository.PostLikeRepository;
import Oops.backend.domain.post.repository.SpecFeedRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.entity.UserAndCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecFeedServiceImpl implements SpecFeedService {

    private final SpecFeedRepository specFeedRepository;
    private final UserAndCategoryRepository userAndCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final RandomTopicRepository randomTopicRepository;
    private final S3ImageService s3ImageService;
    private final PostLikeRepository postLikeRepository;

    /**
     * 베스트 실패담 50개 정렬 기준에 맞춰 조회
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getBestPostList(User user, LocalDateTime cutoff, Pageable pageable, SortType sort){
        // 1. 베스트 50개 확정
        Page<Post> bestPosts = specFeedRepository.findTopBestPosts(
                PageRequest.of(0, 50, Sort.unsorted())
        );

        if (bestPosts.isEmpty()) {
            throw new GeneralException(ErrorStatus.NO_POST);
        }

        // 2. sort 기준 정렬
        List<Post> best50 = new ArrayList<>(bestPosts.getContent()); // bestPosts.getContent()는 수정 불가능한 리스트
        if (sort != SortType.BEST) { // BEST면 그대로(베스트 점수순 유지)
            if (sort == SortType.COMMENT){
                List<Long> bestIds = specFeedRepository.findTop50BestPostIds(PageRequest.of(0, 50));

                if (bestIds.isEmpty()) throw new GeneralException(ErrorStatus.NO_POST);

                List<Post> sorted = specFeedRepository.findPostsInIdsOrderByCommentCount(bestIds);

                // pageable 적용 (50개 안에서)
                int start = (int) pageable.getOffset();
                if (start >= sorted.size()) {
                    return toPreviewListDto(new PageImpl<>(List.of(), pageable, sorted.size()), "베스트 Failers", user);
                }
                int end = Math.min(start + pageable.getPageSize(), sorted.size());

                Page<Post> paged = new PageImpl<>(sorted.subList(start, end), pageable, sorted.size());
                return toPreviewListDto(paged, "베스트 Failers", user);
            }
            best50.sort(toComparator(sort));
        }

        // 3. pageable 적용
        int start = (int) pageable.getOffset();
        // page offset 범위 체크
        if (start >= best50.size()) {
            return toPreviewListDto(new PageImpl<>(List.of(), pageable, best50.size()), "베스트 Failers", user);
        }
        int end = Math.min(start + pageable.getPageSize(), best50.size());

        List<Post> content = best50.subList(start, end);

        Page<Post> pagedPosts = new PageImpl<>(
                content,
                pageable,
                best50.size()
        );

        return toPreviewListDto(pagedPosts, "베스트 Failers", user);
    }

    /**
     * 즐겨찾기한 카테고리 게시글 정렬 기준에 따라 조회
     * categoryId == 0 : 즐겨찾기 전체 카테고리
     * categoryId > 0  : 특정 즐겨찾기 카테고리
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getMarkedPostList(Situation situation, LocalDateTime cutoff, Pageable pageable, User user, SortType sort, Long categoryId){
        /** 로그인하지 않은 사용자의 경우 null 리스트 반환 */
        if (user == null){
            return toPreviewListDto(null, "로그인한 사용자만 이용할 수 있습니다.", user);
        }

        List<Long> categoryIds = new ArrayList<>();

        if (categoryId == 0) { /** 즐겨찾기 전체 카테고리에서 조회 */
            categoryIds = userAndCategoryRepository.findCategoryIdsByUser(user);
            if (categoryIds.isEmpty()) throw new GeneralException(ErrorStatus.NO_BOOKMARKED);
        } else { /** 특정 카테고리 존재 검증 */
            if (!categoryRepository.existsById(categoryId)) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST, "존재하지 않는 카테고리입니다.");
            }
            userAndCategoryRepository.findByUserIdAndCategoryId(user.getId(), categoryId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST, "사용자가 해당 카테고리를 즐겨찾기하지 않습니다."));

            categoryIds.add(categoryId);
        }

        // 댓글순 정렬인 경우
        if (sort == SortType.COMMENT) {
            Page<Post> posts = specFeedRepository.findMarkedPostsOrderByCommentCount(
                    categoryIds, situation, cutoff,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted())
            );
            return toPreviewListDto(posts, "즐겨찾기한 실패담 " + sort + "순", user);
        }

        // 다른 정렬 기준인 경우 Sort 이용
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                toSort(sort)
        );

        Page<Post> posts = specFeedRepository
                .findByCategoryIdInAndSituationAndCreatedAtBefore(
                        categoryIds, situation, cutoff, sortedPageable
                );

        return toPreviewListDto(posts, "즐겨찾기한 실패담 " + sort + "순", user);
    }

    /**
     * 카테고리별 피드
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getPostByCategoryList(User user, Situation situation, LocalDateTime cutoff, Pageable pageable, Long categoryId, SortType sort){

        String categoryName = categoryRepository.findNameById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 댓글순 정렬인 경우
        if (sort == SortType.COMMENT) {
            Page<Post> posts = specFeedRepository.findCategoryPostsOrderByCommentCount(
                    categoryId, situation, cutoff,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted())
            );
            return toPreviewListDto(posts, categoryName + " 카테고리", user);
        }

        // 다른 정렬 기준인 경우 Sort 이용
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                toSort(sort)
        );

        Page<Post> posts = specFeedRepository.findByCategoryIdAndSituationAndCreatedAtBeforeWithCategory(categoryId, situation, cutoff, sortedPageable);

        return toPreviewListDto(posts, categoryName + " 카테고리", user);
    }

    /**
     * 이번주 랜덤 주제 피드
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getThisWeekPostList(User user, Situation situation, LocalDateTime cutoff, Pageable pageable, SortType sort){

        // 이번주 랜덤 주제 조회
        RandomTopic currentTopic = randomTopicRepository.findCurrentTopic()
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_CURRENT_TOPIC));

        Long currentTopicId = currentTopic.getId();

        String topicName = randomTopicRepository.findNameById(currentTopicId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TOPIC_NOT_FOUND));

        // 이번주 주제에 맞는 게시글 조회 (정렬기준에 따라)
        // 댓글순 정렬인 경우
        if (sort == SortType.COMMENT) {
            Page<Post> posts = specFeedRepository.findTopicPostsOrderByCommentCount(
                    currentTopicId, situation, cutoff,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted())
            );
            return toPreviewListDto(posts, topicName, user);
        }

        // 다른 정렬 기준인 경우 Sort 이용
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                toSort(sort)
        );

        Page<Post> posts = specFeedRepository.findByTopicIdAndSituationAndCreatedAtBefore(currentTopicId, situation, cutoff, sortedPageable);

        return toPreviewListDto(posts, topicName, user);
    }

    /**
     * 저번주 랜덤 주제 피드
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse.PostPreviewListDto> getLastWeekPostList(User user, Situation situation, LocalDateTime cutoff, Pageable pageable, SortType sort){

        // 이번주 랜덤 주제 조회
        RandomTopic currentTopic = randomTopicRepository.findCurrentTopic()
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_CURRENT_TOPIC));

        // 저번주 랜덤 주제 아이디 조회
        Long lastTopicId = currentTopic.getLastRandomTopic().getId();

        // 결과 저장할 리스트
        List<PostResponse.PostPreviewListDto> result = new ArrayList<>();

        // 저번주 랜덤 주제 이름
        String topicName = randomTopicRepository.findNameById(lastTopicId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TOPIC_NOT_FOUND));

        // top 3 실패담 조회
        Page<Post> bestPosts = specFeedRepository.findTop3BestPostsByTopic(lastTopicId, cutoff, PageRequest.of(0, 3));
        if (bestPosts.isEmpty()){
            throw new GeneralException(ErrorStatus.NO_POST, "TOP 3 실패담이 없습니다.");
        }
        PostResponse.PostPreviewListDto bestPostDto = toPreviewListDto(bestPosts, "최고의 " + topicName + " 실패담 top 3", user);
        result.add(bestPostDto);

        // top 3 실패담의 아이디 수집
        List<Long> bestPostIds = bestPosts.stream()
                .map(Post::getId)
                .toList();

        // 나머지 게시글 정렬 기준에 따라 조회 (top 3 제외)
        // 댓글순 정렬인 경우
        if (sort == SortType.COMMENT) {
            Page<Post> posts = specFeedRepository.findLastTopicPostsOrderByCommentCount(
                    lastTopicId, situation, cutoff, bestPostIds,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted())
            );
            PostResponse.PostPreviewListDto postDto = toPreviewListDto(posts, topicName, user);
            result.add(postDto);
            return result;
        }

        // 다른 정렬 기준인 경우 Sort 이용
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                toSort(sort)
        );

        Page<Post> posts = specFeedRepository.findFilteredPostsExcludingIds(
                lastTopicId, situation, cutoff, bestPostIds, sortedPageable);

        // posts가 없으면 예외
        if (posts.isEmpty()) {
            throw new GeneralException(ErrorStatus.NO_POST, "TOP 3을 제외한 실패담이 없습니다.");
        }

        // posts 추가
        PostResponse.PostPreviewListDto postDto = toPreviewListDto(posts,  topicName + " 실패담", user);
        result.add(postDto);

        return result;
    }

    /**
     * 결과 DTO 변환 메서드
     */
    private PostResponse.PostPreviewListDto toPreviewListDto(Page<Post> posts, String listName, User user){

        if (posts == null) {
            return PostResponse.PostPreviewListDto.builder()
                    .comment(listName)
                    .posts(Collections.emptyList())
                    .isLast(true) // null일 경우 더 이상 페이지 없음으로 처리
                    .build();
        }

        final Set<Long> likedPostIds =
                (user != null && !posts.isEmpty())
                        ? new HashSet<>(
                        postLikeRepository.findLikedPostIds(
                                user,
                                posts.stream().map(Post::getId).toList()
                        )
                )
                        : Collections.emptySet();

        List<PostResponse.PostPreviewDto> previews = posts.stream()
                .map(post -> {
                    String name = (post.getCategory() != null && post.getTopic() == null)
                            ? post.getCategory().getName()
                            : (post.getCategory() == null && post.getTopic() != null)
                            ? post.getTopic().getName()
                            : null;

                    if (name == null) {
                        throw new GeneralException(ErrorStatus.POST_CATEGORY_TOPIC_INVALID,
                                "카테고리 / 랜덤 주제 설정이 잘못된 게시글입니다.");
                    }

                    String imageUrl = null;
                    if (post.getImages() != null && !post.getImages().isEmpty()) {
                        imageUrl = s3ImageService.getPreSignedUrl(post.getImages().get(0));
                    }

                    Boolean isLiked = (user == null) ? null : likedPostIds.contains(post.getId());

                    return PostResponse.PostPreviewDto.from(post, name, imageUrl, isLiked);
                })
                .toList();

        return PostResponse.PostPreviewListDto.builder()
                .comment(listName)
                .posts(previews)
                .isLast(posts.isLast())
                .build();
    }

    /**
     * enum -> Sort로 변경
     */
    private Sort toSort(SortType sortType) {
        return switch (sortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case LIKE   -> Sort.by(Sort.Direction.DESC, "likes");
            case VIEW   -> Sort.by(Sort.Direction.DESC, "watching");
            case COMMENT -> throw new IllegalArgumentException("COMMENT는 전용 쿼리 사용");
            case BEST -> throw new IllegalArgumentException("BEST는 전용 정렬 로직/쿼리 사용");
        };
    }

    /**
     * enum -> Comparator로 변경
     */
    private Comparator<Post> toComparator(SortType sortType) {
        return switch (sortType) {
            case LATEST -> Comparator.comparing(
                            (Post post) -> Optional.ofNullable(post.getCreatedAt()).orElse(LocalDateTime.MIN)
                    ).reversed();
            case LIKE -> Comparator.comparing(
                            (Post post) -> Optional.ofNullable(post.getLikes()).orElse(0)
                    ).reversed();
            case VIEW -> Comparator.comparing(
                            (Post post) -> Optional.ofNullable(post.getWatching()).orElse(0)
                    ).reversed();
            case COMMENT -> throw new IllegalArgumentException("COMMENT는 전용 정렬 로직/쿼리 사용");
            case BEST -> throw new IllegalArgumentException("BEST는 전용 정렬 로직/쿼리 사용");
        };
    }
}
