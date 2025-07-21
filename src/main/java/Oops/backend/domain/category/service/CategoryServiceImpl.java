package Oops.backend.domain.category.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.category.dto.CategoryResponse;
import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.category.repository.UserAndCategoryRepository;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.entity.UserAndCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserAndCategoryRepository userAndCategoryRepository;

    /**
     * 카테고리 전체 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse.CategoryResponseDto> getCategories(User user){

        // 1. 전체 카테고리 조회 (15개 고정)
        List<Category> allCategories = categoryRepository.findAll();

        // 2. 사용자 즐겨찾기한 카테고리 ID 목록 조회
        List<Long> userCategoryIds = userAndCategoryRepository.findCategoryIdsByUser(user);
        Set<Long> storedSet = new HashSet<>(userCategoryIds);    // 빠른 검색을 위해 Set으로

        // 3. 결과 조합
        return allCategories.stream()
                .map(category -> new CategoryResponse.CategoryResponseDto(
                        category.getId(),
                        category.getName(),
                        storedSet.contains(category.getId()) // 즐겨찾기 여부
                ))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 검색
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse.CategoryResponseDto> searchCategory (String searchName, User user) {
        // 이름 기준 카테고리 검색
        List<Category> matchedCategories = categoryRepository.findByNameContainingIgnoreCase(searchName);

        // 매칭되는 검색어가 있는지 확인
        if (matchedCategories.isEmpty()) {
            throw new GeneralException(ErrorStatus.SEARCH_RESULT_NOT_FOUND);
        }

        // 사용자의 즐겨찾기 카테고리 조회
        List<UserAndCategory> userCategories = userAndCategoryRepository.findByUserId(user.getId());
        Set<Long> favoriteCategoryIds = userCategories.stream()
                .map(uc -> uc.getCategory().getId())
                .collect(Collectors.toSet());

        // 결과 조합
        return matchedCategories.stream()
                .map(category -> new CategoryResponse.CategoryResponseDto(
                        category.getId(),
                        category.getName(),
                        favoriteCategoryIds.contains(category.getId()) // 즐겨찾기 여부
                ))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 즐겨찾기 설정
     */
    @Override
    @Transactional
    public void addFavoriteCategory(Long categoryId, User user){
        checkValid(categoryId);

        // 요청에 맞는 카테고리 검색
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 결과 카테고리의 즐겨찾기 여부 조회
        boolean alreadyExists = userAndCategoryRepository.existsByUserIdAndCategoryId(user.getId(), categoryId);
        if (alreadyExists) {
            throw new GeneralException(ErrorStatus.ALREADY_FAVORITE_CATEGORY);
        }

        // 즐겨찾기 등록
        UserAndCategory userAndCategory = UserAndCategory.builder()
                .user(user)
                .category(category)
                .build();

        userAndCategoryRepository.save(userAndCategory);
    }

    /**
     * 카테고리 즐겨찾기 해제
     */
    @Override
    @Transactional
    public void deleteFavoriteCategory(Long categoryId, User user) {

        checkValid(categoryId);

        // 해당 유저-카테고리 즐겨찾기 존재 여부 확인
        UserAndCategory userAndCategory = userAndCategoryRepository.findByUserIdAndCategoryId(user.getId(), categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_FAVORITE_CATEGORY));

        // 삭제
        userAndCategoryRepository.delete(userAndCategory);
    }

    private void checkValid(Long categoryId){
        // 요청 유효성 검증
        if (categoryId == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }else if (categoryId < 1 || categoryId > 15){
            throw new GeneralException(ErrorStatus.INVALID_CATEGORY_ID);
        }
    }
}
