package Oops.backend.domain.failurewiki.service;

import Oops.backend.domain.failurewiki.dto.FailureKeywordDto;
import Oops.backend.domain.failurewiki.dto.FailureKeywordMainResponseDto;

public interface FailureWikiQueryService {
    FailureKeywordMainResponseDto getMainPage();
    FailureKeywordDto findByKeyword(String keyword);
}
