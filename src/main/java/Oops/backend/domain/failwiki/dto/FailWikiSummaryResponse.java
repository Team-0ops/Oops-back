package Oops.backend.domain.failwiki.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FailWikiSummaryResponse {

    private String keyword;   // 검색 키워드
    private String summary;   // AI 요약 결과
    private String aiTip;     // (30개 미만 시) AI 한마디
    private Integer postCount; // 해당 키워드 게시글 개수
}
