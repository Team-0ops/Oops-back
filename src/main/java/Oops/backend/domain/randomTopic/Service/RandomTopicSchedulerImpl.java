package Oops.backend.domain.randomTopic.Service;

import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RandomTopicSchedulerImpl {
    private final RandomTopicRepository randomTopicRepository;

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00시
    @Transactional
    public void updateWeeklyTopic() {
        // 1. 모든 주제 초기화
        randomTopicRepository.resetAllCurrent();

        // 2. 현재 isCurrent == 1인 주제 찾기
        RandomTopic current = randomTopicRepository.findCurrentTopic()
                .orElseThrow(() -> new IllegalStateException("현재 주제가 설정되어 있지 않습니다."));

        // 3. 다음 주제 선택
        RandomTopic next = current.getNextRandomTopic();
        if (next == null) throw new IllegalStateException("next_random_id가 설정되어 있지 않습니다.");

        // 4. 다음 주제를 isCurrent = 1로 변경
        next.setIsCurrent(1);
    }
}
