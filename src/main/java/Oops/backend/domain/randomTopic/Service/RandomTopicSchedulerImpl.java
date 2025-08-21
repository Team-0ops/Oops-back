package Oops.backend.domain.randomTopic.Service;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.category.repository.CategoryRepository;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.HomeFeedRepository;
import Oops.backend.domain.post.repository.PostRepository;
import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.domain.postGroup.repository.PostGroupRepository;
import Oops.backend.domain.randomTopic.Repository.RandomTopicRepository;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RandomTopicSchedulerImpl {
    private final RandomTopicRepository randomTopicRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostGroupRepository postGroupRepository;

    //@Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00시
    @Scheduled(cron = "0 * * * * *") // 디버깅용 - 매 분 0초에 실행
    @Transactional
    public void updateWeeklyTopic() {

        // 현재 isCurrent == 1인 주제 찾기
        RandomTopic current = randomTopicRepository.findCurrentTopic()
                .orElseThrow(() -> new IllegalStateException("현재 주제가 설정되어 있지 않습니다."));

        Category freeCategory = categoryRepository.findById(15L)
                .orElseThrow(() -> new IllegalArgumentException("자유 카테고리를 찾을 수 없습니다."));

        postGroupRepository.setGroupCategoryIfNullByTopic(current.getId(), freeCategory);

        // Post들을 자유 카테고리로 이동 + topic null
        postRepository.movePostsToFree(current.getId(), freeCategory);

        // 모든 토픽 current 초기화 (벌크라 clear 자동 권장)
        randomTopicRepository.resetAllCurrent();

        // 다음 토픽 isCurrent=1
        RandomTopic next = current.getNextRandomTopic();
        if (next == null) throw new IllegalStateException("next_random_id가 설정되어 있지 않습니다.");
        next = randomTopicRepository.findById(next.getId())
                .orElseThrow(() -> new IllegalStateException("다음 주제를 찾을 수 없습니다."));
        next.setIsCurrent(1);
    }
}
