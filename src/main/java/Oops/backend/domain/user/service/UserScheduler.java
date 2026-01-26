package Oops.backend.domain.user.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.SpecFeedRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserScheduler {
    private final SpecFeedRepository specFeedRepository;
    private final RandomTopicRepository randomTopicRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00시
    //@Scheduled(cron = "0 * * * * *") // 디버깅용 - 매 분 0초에 실행
    @Transactional
    public void updateWeeklyUserPoint() {

        // 이번주 랜덤 주제 조회
        RandomTopic currentTopic = randomTopicRepository.findCurrentTopic()
                .orElseThrow(() -> new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR));

        // 저번주 랜덤 주제 조회
        RandomTopic lastTopic = currentTopic.getLastRandomTopic();
        Long lastTopicId = lastTopic.getId();

        // 해당 사용자 저번주 인기글 선정 여부 확인
        LocalDateTime cutoff = LocalDate.now().atStartOfDay().minusSeconds(1);

        List<Post> bestPosts = specFeedRepository
                .findTop3BestPostsByTopic(lastTopicId, cutoff, PageRequest.of(0, 3))
                .getContent();

        // 사용자별 count 집계
        Map<Long, Long> postCountByUserId = bestPosts.stream()
                .collect(Collectors.groupingBy(
                        post -> post.getUser().getId(),
                        Collectors.counting()
                ));

        // 포인트 지급 (1건당 50점)
        for (Map.Entry<Long, Long> entry : postCountByUserId.entrySet()) {
            Long userId = entry.getKey();
            Long count = entry.getValue();

            userRepository.findById(userId).ifPresent(user -> {
                int newPoint = user.getPoint() + (int)(count * 50);
                user.setPoint(newPoint);
                user.setIsSelected(true);
                userRepository.save(user);
            });
        }
    }
}
