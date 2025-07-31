package Oops.backend.domain.category.dto;

import Oops.backend.domain.category.entity.Category;
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

        public static CategoryResponseDto from(Category category){
            return CategoryResponseDto.builder()
                    .categoryId(category.getId())
                    .name(category.getName())
                    .build();
        }
    }
}
