package Oops.backend.domain.failwiki.service;

import Oops.backend.domain.failwiki.dto.FailWikiSummaryResponse;
import Oops.backend.domain.failwiki.entity.FailWikiSummary;
import Oops.backend.domain.failwiki.repository.FailWikiSummaryRepository;
import Oops.backend.domain.post.dto.PostSummaryDto;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.model.Situation;
import Oops.backend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FailWikiServiceImpl implements FailWikiService {

    private final FailWikiSummaryRepository failWikiSummaryRepository;
    private final PostRepository postRepository;
    private final GptService gptService;

    @Override
    @Transactional
    public FailWikiSummaryResponse getSummary(String keyword) {
        return failWikiSummaryRepository.findByKeyword(keyword)
                .map(s -> FailWikiSummaryResponse.builder()
                        .keyword(s.getKeyword())
                        .summary(s.getSummary())
                        .aiTip(s.getAiTip())
                        .postCount(s.getPostCount())
                        .bestFailers(getBestFailers())
                        .build())
                .orElseGet(() -> generateAndSaveSummary(keyword));
    }
    private List<PostSummaryDto> getBestFailers() {
        List<Situation> bestSituations = List.of(Situation.OOPS, Situation.OVERCOMING, Situation.OVERCOME);
        return postRepository.findBestFailers(bestSituations, PageRequest.of(0, 6))
                .stream()
                .map(PostSummaryDto::from)
                .toList();
    }

    private FailWikiSummaryResponse generateAndSaveSummary(String keyword) {
        List<Post> posts = postRepository.findOvercomingOrOvercomePostsByKeyword(keyword);

        String summary = null;
        String aiTip = null;
        FailWikiSummary summaryEntity;

        if (posts.size() >= 30) {
            // 30개 이상이면 전체 요약 생성
            summary = gptService.summarizePosts(
                    posts.stream().map(Post::getContent).toList(),
                    keyword,
                    true
            );

            summaryEntity = FailWikiSummary.builder()
                    .keyword(keyword)
                    .summary(summary)
                    .aiTip(null) // 30개 이상일 때는 aiTip 저장 안 함
                    .postCount(posts.size())

                    .build();

        } else {
            // 30개 미만이면 조회수 TOP 5 선택
            List<Post> top5 = posts.stream()
                    .sorted(Comparator.comparing(Post::getWatching).reversed())
                    .limit(5)
                    .toList();

            aiTip = gptService.aiOneLineTip(keyword);

            summaryEntity = FailWikiSummary.builder()
                    .keyword(keyword)
                    .summary(null) // 30개 미만일 시 summary 저장 안 함
                    .aiTip(aiTip)
                    .postCount(posts.size())
                    .build();
        }

        failWikiSummaryRepository.save(summaryEntity);

        // 30개 이상일시
        if (posts.size() >= 30) {
            return FailWikiSummaryResponse.builder()
                    .keyword(keyword)
                    .summary(summary)
                    .postCount(posts.size())
                    .bestFailers(getBestFailers())
                    .build();
        } else { //30개 미만일시
            return FailWikiSummaryResponse.builder()
                    .keyword(keyword)
                    .aiTip(aiTip)
                    .postCount(posts.size())
                    .bestFailers(getBestFailers())
                    .build();
        }
    }
}
