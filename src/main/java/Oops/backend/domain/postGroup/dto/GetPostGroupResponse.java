package Oops.backend.domain.postGroup.dto;

import Oops.backend.domain.category.dto.CategoryResponse;
import Oops.backend.domain.post.dto.PostResponse;
import lombok.Builder;
import lombok.Getter;


@Getter
public class GetPostGroupResponse {

    // 그룹 ID
    Long groupId;

    // 카테고리 ID
    CategoryResponse.CategoryResponseDto category;

    // 웁스 중
    PostResponse.PostViewDto postFailure;

    // 극복 중
    PostResponse.PostViewDto postOvercoming;

    // 극복 완료
    PostResponse.PostViewDto postOvercome;

    @Builder
    private GetPostGroupResponse(Long groupId,
                                 CategoryResponse.CategoryResponseDto category,
                                 PostResponse.PostViewDto postFailure,
                                 PostResponse.PostViewDto postOvercoming,
                                 PostResponse.PostViewDto postOvercome){
        this.groupId = groupId;
        this.category = category;
        this.postFailure = postFailure;
        this.postOvercoming = postOvercoming;
        this.postOvercome = postOvercome;
    }

    public static GetPostGroupResponse of(Long groupId,
                                          CategoryResponse.CategoryResponseDto category,
                                          PostResponse.PostViewDto postFailure,
                                          PostResponse.PostViewDto postOvercoming,
                                          PostResponse.PostViewDto postOvercome){

        return GetPostGroupResponse.builder()
                .groupId(groupId)
                .category(category)
                .postFailure(postFailure)
                .postOvercoming(postOvercoming)
                .postOvercome(postOvercome)
                .build();

    }
}
