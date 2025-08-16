package Oops.backend.domain.randomTopic.dto;

import Oops.backend.domain.randomTopic.entity.RandomTopic;
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

    @Getter
    public static class RandomTopicOfPostDto{

        Long randomTopicId;
        String randomTopicName;

        @Builder
        private RandomTopicOfPostDto(RandomTopic randomTopic){
            this.randomTopicId = randomTopic.getId();
            this.randomTopicName = randomTopic.getName();
        }

        public static RandomTopicOfPostDto from(RandomTopic randomTopic){

            if (randomTopic == null) return null;

            return RandomTopicOfPostDto.builder()
                    .randomTopic(randomTopic)
                    .build();
        }

    }
}
