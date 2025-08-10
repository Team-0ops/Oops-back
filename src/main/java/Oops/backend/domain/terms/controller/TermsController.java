package Oops.backend.domain.terms.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.terms.dto.response.TermsResponse;
import Oops.backend.domain.terms.service.TermsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "약관 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;

    @Operation(summary = "이용약관 전체 조회", description = "3개의 이용약관(필수/선택 포함)을 반환합니다.")
    @GetMapping("/terms")
    public ResponseEntity<BaseResponse> getTerms() {
        List<TermsResponse> terms = termsService.getAllTerms();
        return BaseResponse.onSuccess(SuccessStatus._OK, terms);
    }
}
