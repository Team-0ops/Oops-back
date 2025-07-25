package Oops.backend.domain.randomTopic.Service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.post.repository.SpecFeedRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.dto.RandomTopicResponse;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RandomTopicServiceImpl implements RandomTopicService {

    private final RandomTopicRepository randomTopicRepository;
    private final SpecFeedRepository SpecFeedRepository;

    @Override
    @Transactional(readOnly = true)
    public RandomTopicResponse.BannarsInfoDto getBannarInfo(Long lastTopicId, User user){
        // 요청 유효성 검증
        if (lastTopicId == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }else if (lastTopicId < 1 || lastTopicId > 20) {
            throw new GeneralException(ErrorStatus.INVALID_TOPIC_ID);
        }

        // 지난주, 이번주 랜덤 주제 조회
        RandomTopic lastTopic = randomTopicRepository.findById(lastTopicId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR));

        RandomTopicResponse.TopicInfoDto lastTopicInfoDto = RandomTopicResponse.TopicInfoDto.builder()
                .informNum(1)
                .topicId(lastTopic.getId())
                .topicName(lastTopic.getName())
                .topicIcon(lastTopic.getImage())
                .build();

        Long currentTopicId = lastTopic.getNextTopicId();

        RandomTopic currentTopic = randomTopicRepository.findById(currentTopicId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR));

        RandomTopicResponse.TopicInfoDto currentTopicInfoDto = RandomTopicResponse.TopicInfoDto.builder()
                .informNum(2)
                .topicId(currentTopic.getId())
                .topicName(currentTopic.getName())
                .topicIcon(currentTopic.getImage())
                .build();

        // 사용자가 저번주 주제에 대하여 인기글에 선정되었는지 여부
        LocalDateTime cutoff = LocalDate.now().atStartOfDay().minusSeconds(1);
        Page<Post> bestPosts = SpecFeedRepository.findTop3BestPostsByTopic(lastTopicId, cutoff, PageRequest.of(0, 3));

        boolean isBestUser = bestPosts.stream()
                .anyMatch(post -> post.getUser().getId().equals(user.getId()));

        // 결과 반환
        return RandomTopicResponse.BannarsInfoDto.builder()
                .lastTopicInfo(lastTopicInfoDto)
                .currentTopicInfo(currentTopicInfoDto)
                .isBestUser(isBestUser)
                .build();
    }
}