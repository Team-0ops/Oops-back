package Oops.backend.domain.post.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.category.repository.UserAndCategoryRepository;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.SpecFeedRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecFeedServiceImpl implements SpecFeedService {

    private final SpecFeedRepository specFeedRepository;
    private final UserAndCategoryRepository userAndCategoryRepository;
    private final CategoryRepository categoryRepository;

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

        return toPreviewListDto(posts, "베스트 실패담");
    }

    /**
     * 즐겨찾기한 카테고리 게시글 전체 조회
     */
    @Override
    @Transactional
    public PostResponse.PostPreviewListDto getMarkedPostList(LocalDateTime cutoff, Pageable pageable, User user){
        // 사용자가 즐겨찾기한 카테고리 아이디 조회
        List<Long> userCategoryIds = userAndCategoryRepository.findCategoryIdsByUser(user);
        if (userCategoryIds.isEmpty()){
            throw new GeneralException(ErrorStatus.NO_BOOKMARKED);
        }

        // 즐찾 카테고리의 게시글 최신순 정렬하여 allPosts에 저장
        Page<Post> posts = specFeedRepository.findByCategoryIdInAndCreatedAtBefore(userCategoryIds, cutoff, pageable);

        return toPreviewListDto(posts, "즐겨찾기한 실패담");
    }

    /**
     * 카테고리별 피드
     */
    @Override
    @Transactional
    public PostResponse.PostPreviewListDto getPostByCategoryList(LocalDateTime cutoff, Pageable pageable, Long categoryId){

        String categoryName = categoryRepository.findNameById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));

        Page<Post> posts = specFeedRepository.findByCategoryIdAndCreatedAtBeforeWithCategory(categoryId, cutoff, pageable);

        return toPreviewListDto(posts, categoryName + " 실패담");
    }

    /**
     * 결과 DTO 변환 메서드
     */
    private PostResponse.PostPreviewListDto toPreviewListDto(Page<Post> posts, String listName){

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
