package Oops.backend.domain.category.controller;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.category.dto.CategoryResponse;
import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.service.CategoryService;
import Oops.backend.domain.auth.AuthenticationContext;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final AuthenticationContext authenticationContext;

    /**
     * 전체 카테고리명 조회
     */
    @GetMapping
    @Operation(summary = "전체 카테고리 조회 API",description = "전체 15개의 카테고리를 조회하는 api이며, 카테고리 이름과 즐겨찾기 여부를 반환합니다.")
    public ResponseEntity<BaseResponse> getCategoryList() {
        User currentUser = authenticationContext.getPrincipal();
        if (currentUser == null){
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }

        List<CategoryResponse.CategoryResponseDto> result = categoryService.getCategories(currentUser);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 카테고리 검색
     */
    @GetMapping("/search")
    @Operation(summary = "카테고리 검색 API",description = "카테고리를 이름으로 검색하는 API로 즐겨찾기 여부까지 반환됩니다.")
    public ResponseEntity<BaseResponse> searchCategoriesByName(@RequestParam("name") String searchName) {
        User currentUser = authenticationContext.getPrincipal();
        // 사용자가 존재하는지 확인
        if (currentUser == null){
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
        // 검색어가 비어 있지 않은지 확인
        if (searchName == null || searchName.isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_SEARCH_KEYWORD);
        }

        List<CategoryResponse.CategoryResponseDto> result = categoryService.searchCategory(searchName, currentUser);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }

    /**
     * 카테고리 즐겨찾기 추가
     */
    @PostMapping("/{categoryId}/bookmark")
    @Operation(summary = "카테고리 즐겨찾기 설정 API",description = "선택된 카테고리를 즐겨찾기로 설정합니다.")
    public ResponseEntity<BaseResponse> bookmarked (@PathVariable Long categoryId) {
        User currentUser = authenticationContext.getPrincipal();
        // 사용자가 존재하는지 확인
        if (currentUser == null){
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
        categoryService.addFavoriteCategory(categoryId, currentUser);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 카테고리 즐겨찾기 삭제
     */
    @DeleteMapping("/{categoryId}/unbookmark")
    @Operation(summary = "카테고리 즐겨찾기 해제 API",description = "선택된 카테고리를 즐겨찾기 해제합니다.")
    public ResponseEntity<BaseResponse> unbookmarked (@PathVariable Long categoryId) {
        User currentUser = authenticationContext.getPrincipal();
        // 사용자가 존재하는지 확인
        if (currentUser == null){
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
        categoryService.deleteFavoriteCategory(categoryId, currentUser);
        return BaseResponse.onSuccess(SuccessStatus._OK);
    }

}