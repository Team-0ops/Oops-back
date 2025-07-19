package Oops.backend.domain.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryResponse {

    /**
     * 카테고리 조회
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponseDto {
        Long categoryId;
        String name;
        boolean isStored;    // 즐겨찾기 여부
    }
}
