package Oops.backend.domain.luckyDraw.controller;

import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.SuccessStatus;
import Oops.backend.domain.auth.AuthenticatedUser;
import Oops.backend.domain.luckyDraw.dto.LuckyDrawResponse;
import Oops.backend.domain.luckyDraw.service.LuckyDrawService;
import Oops.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lucky-draw")
public class LuckyDrawController {

    private final LuckyDrawService luckyDrawService;

    @PostMapping
    @Operation(summary = "행운부적 추첨 API",description = "사용자 포인트가 150 이상이면 추첨 가능합니다. 가장 최근 뽑힌 3개의 부적은 제외하고 랜덤 추첨합니다.")
    public ResponseEntity<BaseResponse> getLuckyDraw(@Parameter(hidden = true) @AuthenticatedUser User user) {

        LuckyDrawResponse.LuckyDrawResponseDto result = luckyDrawService.getLuckyDraw(user);
        return BaseResponse.onSuccess(SuccessStatus._OK, result);
    }
}
