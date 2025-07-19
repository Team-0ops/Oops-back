package Oops.backend.domain.post.service;

import Oops.backend.domain.category.repository.UserAndCategoryRepository;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.entity.UserAndCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final PostRepository postRepository;
    private final UserAndCategoryRepository userAndCategoryRepository;

    /**
     * 홈화면 첫로딩
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse.PostPreviewListDto> getFirstPostList(User user) {

        List<PostResponse.PostPreviewListDto> result = new ArrayList<>();

        // 1. 전날 23:59까지 작성된 글 중 베스트 실패담 5개 조회
        LocalDateTime cutoff = LocalDate.now().atStartOfDay().minusSeconds(1);
        List<Post> bestPosts = postRepository.findTopBestPostBefore(cutoff, PageRequest.of(0, 5));

        List<PostResponse.PostPreviewDto> bestPreviewDtos = bestPosts.stream()
                .map(this::convertToPreviewDto)
                .collect(Collectors.toList());

        PostResponse.PostPreviewListDto bestListDto = PostResponse.PostPreviewListDto.builder()
                .name("베스트 Failers")
                .posts(bestPreviewDtos)
                .build();

        result.add(bestListDto); // 항상 베스트 리스트는 추가

        // 2. 즐겨찾기한 카테고리의 최신 글 10개 조회
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

        Pageable topTen = PageRequest.of(0, 10);
        List<Post> posts = postRepository.findTop10ByCategoryIdsOrderByCreatedAtDesc(categoryIds, topTen);

        List<PostResponse.PostPreviewDto> markedPreviewDtos = posts.stream()
                .map(this::convertToPreviewDto)
                .collect(Collectors.toList());

        PostResponse.PostPreviewListDto markedListDto = PostResponse.PostPreviewListDto.builder()
                .name("즐겨찾기한 카테고리")
                .posts(markedPreviewDtos)
                .build();

        result.add(markedListDto);

        return result;
    }

    private PostResponse.PostPreviewDto convertToPreviewDto(Post post) {
        return PostResponse.PostPreviewDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryName(post.getCategory().getName())
                .likes(post.getLikes())
                .comments(post.getComments() != null ? post.getComments().size() : 0)
                .views(post.getWatching())
                .image(post.getImages() != null && !post.getImages().isEmpty() ? post.getImages().get(0) : null)
                .build();
    }

    /**
     * 홈화면 이후 로딩
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse.PostPreviewListDto getLaterPostList() {
        List<Post> latestPostPerCategories = postRepository.findLatestPostPerCategory();

        List<PostResponse.PostPreviewDto> previewDtos = latestPostPerCategories.stream()
                .map(this::convertToPreviewDto)
                .collect(Collectors.toList());

        PostResponse.PostPreviewListDto listDto = PostResponse.PostPreviewListDto.builder()
                .name("카테고리 목록")
                .posts(previewDtos)
                .build();

        return listDto;
    }

}
