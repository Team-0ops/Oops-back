package Oops.backend.domain.failurewiki.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.failurewiki.dto.FailureKeywordDto;
import Oops.backend.domain.failurewiki.dto.FailureKeywordMainResponseDto;
import Oops.backend.domain.failurewiki.entity.FailureKeyword;
import Oops.backend.domain.failurewiki.repository.FailureKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FailureWikiQueryServiceImpl implements FailureWikiQueryService {

    private final FailureKeywordRepository failureKeywordRepository;

    @Override
    public FailureKeywordMainResponseDto getMainPage() {
        List<FailureKeyword> keywords = failureKeywordRepository.findRandom(PageRequest.of(0, 5));

        List<FailureKeywordDto> keywordDtos = keywords.stream()
                .map(FailureKeywordDto::from)
                .collect(Collectors.toList());

        return FailureKeywordMainResponseDto.of(keywordDtos);
    }

    //검색어 입력
    @Override
    public FailureKeywordDto findByKeyword(String keyword) {
        FailureKeyword entity = failureKeywordRepository.findByKeyword(keyword)
                .orElseThrow(() -> new GeneralException(
                        ErrorStatus.SEARCH_RESULT_NOT_FOUND,
                        "해당 키워드에 대한 극복 팁이 존재하지 않습니다."
                ));
        return FailureKeywordDto.from(entity);
    }

}