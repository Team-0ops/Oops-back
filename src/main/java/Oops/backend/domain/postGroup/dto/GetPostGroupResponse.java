package Oops.backend.domain.postGroup.dto;

import Oops.backend.domain.category.dto.CategoryResponse;
import Oops.backend.domain.post.dto.PostResponse;
import Oops.backend.domain.randomTopic.dto.RandomTopicResponse;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;


@Getter
public class GetPostGroupResponse {

    // 그룹 ID
    Long groupId;

    // 카테고리 ID
    CategoryResponse.CategoryResponseDto category;

    // 랜덤 주제 ID
    RandomTopicResponse.RandomTopicOfPostDto randomTopic;

    // 웁스 중
    PostResponse.PostViewDto postFailure;

    // 극복 중
    PostResponse.PostViewDto postOvercoming;

    // 극복 완료
    PostResponse.PostViewDto postOvercome;

    @Builder
    private GetPostGroupResponse(Long groupId,
                                 CategoryResponse.CategoryResponseDto category,
                                 RandomTopicResponse.RandomTopicOfPostDto randomTopic,
                                 PostResponse.PostViewDto postFailure,
                                 PostResponse.PostViewDto postOvercoming,
                                 PostResponse.PostViewDto postOvercome){
        this.groupId = groupId;
        this.category = category;
        this.randomTopic = randomTopic;
        this.postFailure = postFailure;
        this.postOvercoming = postOvercoming;
        this.postOvercome = postOvercome;
    }

    public static GetPostGroupResponse of(Long groupId,
                                          CategoryResponse.CategoryResponseDto category,
                                          RandomTopicResponse.RandomTopicOfPostDto randomTopic,
                                          PostResponse.PostViewDto postFailure,
                                          PostResponse.PostViewDto postOvercoming,
                                          PostResponse.PostViewDto postOvercome){

        return GetPostGroupResponse.builder()
                .groupId(groupId)
                .category(category)
                .randomTopic(randomTopic)
                .postFailure(postFailure)
                .postOvercoming(postOvercoming)
                .postOvercome(postOvercome)
                .build();

    }
}
