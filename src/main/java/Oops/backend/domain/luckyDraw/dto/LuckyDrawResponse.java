package Oops.backend.domain.luckyDraw.dto;

import Oops.backend.domain.luckyDraw.entity.LuckyDraw;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LuckyDrawResponse {
    /**
     * 행운부적 추첨 결과
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LuckyDrawResponseDto {
        String name;
        String content;
        String imageUrl;

        public static LuckyDrawResponse.LuckyDrawResponseDto from(LuckyDraw luckyDraw) {
            return LuckyDrawResponse.LuckyDrawResponseDto.builder()
                    .name(luckyDraw.getName())
                    .content(luckyDraw.getContent())
                    .imageUrl(luckyDraw.getImageUrl())
                    .build();
        }
    }
}
