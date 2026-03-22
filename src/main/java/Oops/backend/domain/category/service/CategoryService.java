package Oops.backend.domain.category.service;

import Oops.backend.domain.category.dto.CategoryResponseDto;
import Oops.backend.domain.user.entity.User;

import java.util.List;

public interface CategoryService {
    // 카테고리 전체 조회
    List<CategoryResponseDto> getCategories(User user);

    // 카테고리 검색
    List<CategoryResponseDto> searchCategory(String searchName, User user);

    // 즐겨찾기 설정
    void addFavoriteCategory(Long categoryId, User user);

    // 즐겨찾기 해제
    void deleteFavoriteCategory(Long categoryId, User user);

    // 즐겨찾기한 카테고리 목록 전체 조회
    List<CategoryResponseDto> getBookmarkedCategories(User user);
}
