package Oops.backend.domain.failurewiki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FailureKeywordMainResponseDto {
    private List<FailureKeywordDto> recommendedKeywords;

    public static FailureKeywordMainResponseDto of(List<FailureKeywordDto> keywords) {
        return new FailureKeywordMainResponseDto(keywords);
    }
}

