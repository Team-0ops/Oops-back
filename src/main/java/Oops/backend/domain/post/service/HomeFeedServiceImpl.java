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
import Oops.backend.domain.post.repository.PostRepository;
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

@Service
@RequiredArgsConstructor
public class HomeFeedServiceImpl implements HomeFeedService {
    private final HomeFeedRepository homeFeedRepository;
    private final UserAndCategoryRepository userAndCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final RandomTopicRepository randomTopicRepository;
    private final S3ImageService s3ImageService;

    /**
     * 홈화면 첫로딩 - 로그인 사용자
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse.PostPreviewListDto> getFirstPostList(User user) {

        List<PostResponse.PostPreviewListDto> result = new ArrayList<>();

        // 1. 전날 23:59까지 작성된 글 중 베스트 실패담 5개 조회
        PostResponse.PostPreviewListDto bestListDto = convertBestListDto();
        result.add(bestListDto);

        // 2. 즐겨찾기한 카테고리의 최신 글 5개 조회
        List<UserAndCategory> userCategories = userAndCategoryRepository.findByUserId(user.getId());
        List<Long> categoryIds = userCategories.stream()
                .map(uc -> uc.getCategory().getId())
                .collect(Collectors.toList());

        if (categoryIds.isEmpty()) {  // 즐겨찾기한 카테고리가 없는 경우
            PostResponse.PostPreviewListDto emptyListDto = PostResponse.PostPreviewListDto.builder()
                    .name("즐겨찾기한 카테고리가 없습니다.")
                    .posts(Collections.emptyList())
                    .build();

            result.add(emptyListDto);
            return result;
        }

        Pageable topFive = PageRequest.of(0, 5);
        List<Post> posts = homeFeedRepository.findTop5ByCategoryIdsOrderByCreatedAtDesc(categoryIds, topFive);

        List<PostResponse.PostPreviewDto> markedPreviewDtos = postDtoConverter(posts);

        PostResponse.PostPreviewListDto markedListDto = PostResponse.PostPreviewListDto.builder()
                .name("즐겨찾기한 카테고리")
                .posts(markedPreviewDtos)
                .isLast(true)
                .build();

        result.add(markedListDto);

        return result;
    }

    /**
     * 홈화면 첫로딩 - 게스트 이용자
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse.PostPreviewListDto> getFirstPostListForGuest() {

        List<PostResponse.PostPreviewListDto> result = new ArrayList<>();

        // 1. 전날 23:59까지 작성된 글 중 베스트 실패담 5개 조회
        PostResponse.PostPreviewListDto bestListDto = convertBestListDto();
        result.add(bestListDto);

        // 2. 즐겨찾기 부분은 null
        result.add(
                PostResponse.PostPreviewListDto.builder()
                        .name("즐겨찾기한 카테고리")
                        .posts(Collections.emptyList())
                        .isLast(true)
                        .build()
        );

        return result;
    }

    /**
     * 베스트 실패담 조회 및 dto 변환
     */
    private PostResponse.PostPreviewListDto convertBestListDto(){
        LocalDateTime cutoff = LocalDate.now().atStartOfDay().minusSeconds(1);
        List<Post> bestPosts = homeFeedRepository.findTopBestPostBefore(cutoff, PageRequest.of(0, 5));

        List<PostResponse.PostPreviewDto> bestPreviewDtos = postDtoConverter(bestPosts);

        PostResponse.PostPreviewListDto bestListDto = PostResponse.PostPreviewListDto.builder()
                .name("베스트 Failers")
                .posts(bestPreviewDtos)
                .isLast(true)
                .build();

        return bestListDto;
    }

    /**
     * 홈화면 이후 로딩
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getLaterPostList() {
        List<Post> latestPostPerCategories = homeFeedRepository.findLatestPostPerCategory();

        List<PostResponse.PostPreviewDto> previewDtos = postDtoConverter(latestPostPerCategories);

        PostResponse.PostPreviewListDto listDto = PostResponse.PostPreviewListDto.builder()
                .name("카테고리 목록")
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
    public PostResponse.PostPreviewListDto searchPosts(String keyword, Pageable pageable){
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

        List<PostResponse.PostPreviewDto> previewDtos = postDtoConverter(pagedPosts);

        return PostResponse.PostPreviewListDto.builder()
                .name("검색 결과")
                .posts(previewDtos)
                .isLast(isLast)
                .build();
    }

    /**
     * dto 변환 메서드
     */
    private List<PostResponse.PostPreviewDto> postDtoConverter(List<Post> posts){

        List<PostResponse.PostPreviewDto> bestPreviewDtos = posts.stream()
                .map(post -> {
                    String CategoryOrTopicName;
                    String imageUrl = null;

                    if (post.getCategory() != null && post.getTopic() == null) {        // 카테고리 게시물인 경우
                        CategoryOrTopicName = post.getCategory().getName();
                    } else if (post.getCategory() == null && post.getTopic() != null) {  // 랜덤 주제 게시물인 경우
                        CategoryOrTopicName = post.getTopic().getName();
                    } else{
                        throw new GeneralException(ErrorStatus.POST_CATEGORY_TOPIC_INVALID, "카테고리 / 랜덤 주제 설정이 잘못된 게시글입니다.");
                    }

                    if (post.getImages() != null && !post.getImages().isEmpty()) {
                        String firstKey = post.getImages().get(0);
                        imageUrl = s3ImageService.getPreSignedUrl(firstKey);
                    }
                    return PostResponse.PostPreviewDto.from(post, CategoryOrTopicName, imageUrl);
                })
                .collect(Collectors.toList());
        return bestPreviewDtos;
    }
}
