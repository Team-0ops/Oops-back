package Oops.backend.domain.randomTopic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RandomTopicResponse {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BannarsInfoDto {
        // 저번주 랜덤 주제
        Long lastTopicId;
        String lastTopicName;

        // 이번주 랜덤 주제
        Long currentTopicId;
        String currentTopicName;

        // 저번주 주제에 대한 사용자 당첨 여부
        boolean isBestUser;
    }
}
