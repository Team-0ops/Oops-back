package Oops.backend.domain.failurewiki.dto;

import Oops.backend.domain.failurewiki.entity.FailureKeyword;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FailureKeywordDto {
    private String keyword;
    private String summaryTip;

    public static FailureKeywordDto from(FailureKeyword entity) {
        return FailureKeywordDto.builder()
                .keyword(entity.getKeyword())
                .summaryTip(entity.getSummaryTip())
                .build();
    }
}