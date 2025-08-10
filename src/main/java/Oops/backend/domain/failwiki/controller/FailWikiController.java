package Oops.backend.domain.failwiki.controller;

import Oops.backend.domain.failwiki.dto.FailWikiListItemDto;
import Oops.backend.domain.failwiki.dto.FailWikiSummaryResponse;
import Oops.backend.domain.failwiki.service.FailWikiQueryService;
import Oops.backend.domain.failwiki.service.FailWikiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/failwiki")
@RequiredArgsConstructor
@Tag(name = "실패위키 API", description = "실패위키 GPT 요약/조언 조회 API")
public class FailWikiController {

    private final FailWikiService failWikiService;
    private final FailWikiQueryService failWikiQueryService;

    @Operation(
            summary = "실패위키 요약/조언 조회",
            description = """
            주어진 키워드와 관련된 실패 극복 글을 검색하여 AI 요약 또는 AI 한마디를 반환합니다.
            
            - 게시글이 30개 이상이면: `summary` 필드에 GPT 요약 반환
            - 게시글이 30개 미만이면: `aiTip` 필드에 GPT 한마디 반환
            - `postCount`는 검색된 게시글 수
            """
    )
    @GetMapping("/summary")
    public ResponseEntity<FailWikiSummaryResponse> getFailWikiSummary(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(failWikiService.getSummary(keyword));
    }

    @Operation(summary = "실패위키 전체 조회(저장본만, 최신순, content만)")
    @GetMapping("/all")
    public ResponseEntity<List<FailWikiListItemDto>> listAllLatest() {
        return ResponseEntity.ok(failWikiQueryService.listAllLatest());
    }

}