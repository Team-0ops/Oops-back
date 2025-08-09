package Oops.backend.domain.failwiki.service;

import Oops.backend.domain.failwiki.dto.FailWikiSummaryResponse;
import Oops.backend.domain.failwiki.entity.FailWikiSummary;
import Oops.backend.domain.failwiki.repository.FailWikiSummaryRepository;
import Oops.backend.domain.post.dto.PostSummaryDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FailWikiServiceImpl implements FailWikiService {

    private static final int SUMMARY_THRESHOLD = 30;
    private static final Duration TTL = Duration.ofHours(6);
    private static final List<Situation> SUMM_SITUATIONS =
            List.of(Situation.OVERCOMING, Situation.OVERCOME);

    private final FailWikiSummaryRepository failWikiSummaryRepository;
    private final PostRepository postRepository;
    private final GptService gptService;

    @Override
    @Transactional
    public FailWikiSummaryResponse getSummary(String keyword) {
        return failWikiSummaryRepository.findByKeyword(keyword)
                .map(s -> refreshIfStale(s, keyword))   // 있으면 업데이트 체크 후 필요 시 재생성
                .orElseGet(() -> createFresh(keyword)); // 없으면 생성
    }

    private FailWikiSummaryResponse refreshIfStale(FailWikiSummary s, String keyword) {
        int currentCount = postRepository.countByKeywordAndSituations(keyword, SUMM_SITUATIONS);
        LocalDateTime currentMaxUpdatedAt =
                postRepository.maxUpdatedAtByKeywordAndSituations(keyword, SUMM_SITUATIONS);

        boolean ttlExpired = s.getModifiedAt() == null
                || s.getModifiedAt().isBefore(LocalDateTime.now().minus(TTL));
        boolean countChanged = !Objects.equals(s.getPostCount(), currentCount);
        boolean sourceAdvanced =
                (s.getSourceMaxUpdatedAt() == null && currentMaxUpdatedAt != null)
                        || (s.getSourceMaxUpdatedAt() != null && currentMaxUpdatedAt != null
                        && currentMaxUpdatedAt.isAfter(s.getSourceMaxUpdatedAt()));
        boolean crossedThreshold = (s.getSummary() == null && currentCount >= SUMMARY_THRESHOLD); // aiTip → summary 승격

        if (ttlExpired || countChanged || sourceAdvanced || crossedThreshold) {
            if (currentCount >= SUMMARY_THRESHOLD) {
                // 전체 요약 재생성
                List<Post> posts = postRepository.findByKeywordAndSituationsOrderByWatchingDesc(
                        keyword, SUMM_SITUATIONS, Pageable.unpaged());
                String summary = gptService.summarizePosts(
                        posts.stream().map(Post::getContent).toList(),
                        keyword,
                        true
                );
                s.updateForSummary(currentCount, summary, currentMaxUpdatedAt); // modifiedAt 자동 갱신
            } else {
                // TOP5 선정, AI 한마디
                List<Post> top5 = postRepository.findByKeywordAndSituationsOrderByWatchingDesc(
                        keyword, SUMM_SITUATIONS, PageRequest.of(0, 5));
                String aiTip = gptService.aiOneLineTip(keyword);
                s.updateForAiTip(currentCount, aiTip, currentMaxUpdatedAt);
            }
        }

        return FailWikiSummaryResponse.builder()
                .keyword(s.getKeyword())
                .summary(s.getSummary())
                .aiTip(s.getAiTip())
                .postCount(s.getPostCount())
                .bestFailers(getBestFailers())
                .build();
    }

    private FailWikiSummaryResponse createFresh(String keyword) {
        int count = postRepository.countByKeywordAndSituations(keyword, SUMM_SITUATIONS);
        LocalDateTime maxUpdatedAt =
                postRepository.maxUpdatedAtByKeywordAndSituations(keyword, SUMM_SITUATIONS);

        FailWikiSummary summaryEntity;

        if (count >= SUMMARY_THRESHOLD) {
            List<Post> posts = postRepository.findByKeywordAndSituationsOrderByWatchingDesc(
                    keyword, SUMM_SITUATIONS, Pageable.unpaged());
            String summary = gptService.summarizePosts(
                    posts.stream().map(Post::getContent).toList(),
                    keyword,
                    true
            );

            summaryEntity = FailWikiSummary.builder()
                    .keyword(keyword)
                    .postCount(count)
                    .build()
                    .updateForSummary(count, summary, maxUpdatedAt);

        } else {
            List<Post> top5 = postRepository.findByKeywordAndSituationsOrderByWatchingDesc(
                    keyword, SUMM_SITUATIONS, PageRequest.of(0, 5));
            String aiTip = gptService.aiOneLineTip(keyword);

            summaryEntity = FailWikiSummary.builder()
                    .keyword(keyword)
                    .postCount(count)
                    .build()
                    .updateForAiTip(count, aiTip, maxUpdatedAt);
        }

        failWikiSummaryRepository.save(summaryEntity);

        return FailWikiSummaryResponse.builder()
                .keyword(summaryEntity.getKeyword())
                .summary(summaryEntity.getSummary())
                .aiTip(summaryEntity.getAiTip())
                .postCount(summaryEntity.getPostCount())
                .bestFailers(getBestFailers())
                .build();
    }

    private List<PostSummaryDto> getBestFailers() {
        List<Situation> bestSituations = List.of(Situation.OOPS, Situation.OVERCOMING, Situation.OVERCOME);
        return postRepository.findBestFailers(bestSituations, PageRequest.of(0, 6))
                .stream()
                .map(PostSummaryDto::from)
                .toList();
    }
}

