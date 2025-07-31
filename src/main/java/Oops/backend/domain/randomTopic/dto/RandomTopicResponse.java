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
        TopicInfoDto lastTopicInfo;

        // 이번주 랜덤 주제
        TopicInfoDto currentTopicInfo;

        // 저번주 주제에 대한 사용자 당첨 여부
        boolean isBestUser;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicInfoDto {
        Integer informNum;
        Long topicId;
        String topicName;
        String topicIcon;
    }
}
