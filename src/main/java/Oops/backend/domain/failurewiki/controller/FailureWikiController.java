package Oops.backend.domain.failurewiki.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.failurewiki.dto.FailureKeywordDto;
import Oops.backend.domain.failurewiki.dto.FailureKeywordMainResponseDto;
import Oops.backend.domain.failurewiki.service.FailureWikiQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/failure-wiki")
public class FailureWikiController {

    private final FailureWikiQueryService failureWikiQueryService;

    @GetMapping("/main")
    public ResponseEntity<BaseResponse> getMainPage() {
        FailureKeywordMainResponseDto response = failureWikiQueryService.getMainPage();
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @GetMapping("/keywords/{keyword}")
    public ResponseEntity<BaseResponse> getKeywordDetail(@PathVariable String keyword) {
        FailureKeywordDto response = failureWikiQueryService.findByKeyword(keyword);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }
}
