package Oops.backend.domain.luckyDraw.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.luckyDraw.dto.LuckyDrawResponse;
import Oops.backend.domain.luckyDraw.service.LuckyDrawService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "행운 부적 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lucky-draw")
public class LuckyDrawController {

    private final LuckyDrawService luckyDrawService;

    @PostMapping
    @Operation(summary = "행운부적 추첨 API",description = "사용자 포인트가 150 이상이면 추첨 가능합니다. 가장 최근 뽑힌 3개의 부적은 제외하고 랜덤 추첨합니다.")
    public ResponseEntity<BaseResponse> getLuckyDraw(@Parameter(hidden = true) User user) {

        log.info("Post /api/lucky-draw 호출, User = {}", user.getUserName());

        LuckyDrawResponse.LuckyDrawResponseDto result = luckyDrawService.getLuckyDraw(user);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
}
