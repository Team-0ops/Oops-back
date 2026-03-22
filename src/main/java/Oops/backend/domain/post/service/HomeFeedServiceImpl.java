package Oops.backend.domain.post.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.config.s3.S3ImageService;
import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.category.repository.UserAndCategoryRepository;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.HomeFeedRepository;
import Oops.backend.domain.post.repository.PostLikeRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.entity.UserAndCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static Oops.backend.common.status.ErrorStatus._BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class HomeFeedServiceImpl implements HomeFeedService {
    private final HomeFeedRepository homeFeedRepository;
    private final UserAndCategoryRepository userAndCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final RandomTopicRepository randomTopicRepository;
    private final S3ImageService s3ImageService;
    private final PostLikeRepository postLikeRepository;

    /**
     * 전날 23:59까지 작성된 글 중 베스트 실패담 5개 조회
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getBestPostList(User user) {
        LocalDateTime cutoff = LocalDate.now().atStartOfDay().minusSeconds(1);
        List<Post> bestPosts = homeFeedRepository.findTopBestPostBefore(cutoff, PageRequest.of(0, 5));

        List<PostResponse.PostPreviewDto> bestPreviewDtos = postDtoConverter(bestPosts, user);

        PostResponse.PostPreviewListDto bestListDto = PostResponse.PostPreviewListDto.builder()
                .comment("베스트 Failers")
                .posts(bestPreviewDtos)
                .isLast(true)
                .build();

        return bestListDto;
    }

    /**
     * 홈화면 특정 즐겨찾기 카테고리 실패담 5개 조회
     * categoryId == 0 : 즐겨찾기 전체 카테고리에서 최신 5개
     * categoryId > 0  : 특정 즐겨찾기 카테고리에서 최신 5개
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getBookmarkedPostList(User user, Long categoryId) {
        /** 로그인하지 않은 사용자의 경우 null 리스트 반환 */
        if (user == null){
            return emptyResponse("로그인 후 이용할 수 있습니다.", true);
        }

        if (categoryId == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "categoryId는 필수입니다.");
        }

        Pageable topFive = PageRequest.of(0, 5);

        /** 즐겨찾기 전체 카테고리에서 조회 */
        if (categoryId == 0){
            List<Long> categoryIds = userAndCategoryRepository.findByUserId(user.getId()).stream()
                    .map(uc -> uc.getCategory().getId())
                    .toList();

            // 사용자가 즐겨찾기한 카테고리가 없는 경우
            if (categoryIds.isEmpty()) {
                return emptyResponse("즐겨찾기한 카테고리가 없습니다.", true);
            }

            List<Post> posts = homeFeedRepository.findTop5ByCategoryIdsOrderByCreatedAtDesc(categoryIds, topFive);
            return successResponse("즐겨찾기 카테고리 - 전체", posts, true, user);
        }

        /** 특정 카테고리 존재 검증 */
        if (!categoryRepository.existsById(categoryId)) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST, "카테고리 아이디가 잘못되었습니다.");
        }

        /** 해당 카테고리를 즐겨찾기 했는지 검증 */
        UserAndCategory userCategory = userAndCategoryRepository
                .findByUserIdAndCategoryId(user.getId(), categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST, "사용자가 해당 카테고리를 즐겨찾기하지 않습니다."));

        List<Post> posts = homeFeedRepository.findLatestByCategory(categoryId, topFive);
        return successResponse(userCategory.getCategory().getName() + " 카테고리", posts, true, user);
    }

    /**
     * 홈화면 카테고리별 실패담 1개씩 조회
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getCategoriesPostList(User user) {
        List<Post> latestPostPerCategories = homeFeedRepository.findLatestPostPerCategory();

        List<PostResponse.PostPreviewDto> previewDtos = postDtoConverter(latestPostPerCategories, user);

        PostResponse.PostPreviewListDto listDto = PostResponse.PostPreviewListDto.builder()
                .comment("카테고리 목록")
                .posts(previewDtos)
                .isLast(true)
                .build();

        return listDto;
    }

    /**
     * 실패담 검색
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto searchPosts(User user, String keyword, Pageable pageable){
        if (keyword == null || keyword.isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_SEARCH_KEYWORD);
        }

        // 카테고리 레포에서 검색어가 포함된 카테고리 조회
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(keyword);

        // 카테고리에 포함된 게시글 조회
        List<Post> categoryPosts = categories.isEmpty()
                ? Collections.emptyList()
                : homeFeedRepository.findByCategoryIn(categories);

        // 랜덤주제 레포에서 검색어가 포함된 랜덤주제 조회
        List<RandomTopic> topics = randomTopicRepository.findByNameContainingIgnoreCase(keyword);

        // 랜덤주제에 포함된 게시글 조회
        List<Post> topicPosts = topics.isEmpty()
                ? Collections.emptyList()
                : homeFeedRepository.findByTopicIn(topics);

        // 게시글의 제목/본문에 검색어가 포함된 게시글 조회
        List<Post> keywordPosts = homeFeedRepository.findByKeyword(keyword);

        // 두 결과 합치고 중복 제거
        Set<Post> mergedPosts = new HashSet<>();
        mergedPosts.addAll(categoryPosts);
        mergedPosts.addAll(topicPosts);
        mergedPosts.addAll(keywordPosts);

        // 최신순 정렬 + 페이징 적용
        List<Post> sortedPosts = mergedPosts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());

        int totalSize = sortedPosts.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), totalSize);

        List<Post> pagedPosts = (start > totalSize)
                ? Collections.emptyList()
                : sortedPosts.subList(start, end);

        boolean isLast = end >= totalSize;

        List<PostResponse.PostPreviewDto> previewDtos = postDtoConverter(pagedPosts, user);

        return PostResponse.PostPreviewListDto.builder()
                .comment("검색 결과")
                .posts(previewDtos)
                .isLast(isLast)
                .build();
    }

    /**
     * dto 변환 메서드
     */
    private List<PostResponse.PostPreviewDto> postDtoConverter(List<Post> posts, User user){

        final Set<Long> likedPostIds =
                (user != null && !posts.isEmpty())
                        ? new HashSet<>(
                        postLikeRepository.findLikedPostIds(
                                user,
                                posts.stream().map(Post::getId).toList()
                        )
                )
                        : Collections.emptySet();

        return posts.stream()
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
    }


    // 조회 내용이 없는 경우 응답
    private PostResponse.PostPreviewListDto emptyResponse(String comment, boolean isLast) {
        return PostResponse.PostPreviewListDto.builder()
                .comment(comment)
                .posts(Collections.emptyList())
                .isLast(isLast)
                .build();
    }

    // 성공 응답
    private PostResponse.PostPreviewListDto successResponse(String comment, List<Post> posts, boolean isLast, User user) {
        return PostResponse.PostPreviewListDto.builder()
                .comment(comment)
                .posts(postDtoConverter(posts,user))
                .isLast(isLast)
                .build();
    }
}
