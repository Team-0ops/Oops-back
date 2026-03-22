package Oops.backend.domain.category.dto;

import Oops.backend.domain.category.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
public class CategoryResponseDto {

    Long categoryId;
    String name;
    boolean isStored; // 즐겨찾기 여부

    public static CategoryResponseDto from(Category category){

        if (category==null) return null;

        return CategoryResponseDto.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .build();
    }

    public  CategoryResponseDto(Long id, String name, boolean isBookmarked){
        this.categoryId = id;
        this.name = name;
        this.isStored = isBookmarked;
    }
}