package Oops.backend.domain.failwiki.service;

import Oops.backend.domain.failwiki.dto.FailWikiListItemDto;
import Oops.backend.domain.failwiki.repository.FailWikiSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FailWikiQueryServiceImpl implements FailWikiQueryService {

    private final FailWikiSummaryRepository repo;

    @Override
    @Transactional(readOnly = true)
    public List<FailWikiListItemDto> listAllLatest() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "modifiedAt"))
                .stream()
                .map(s -> FailWikiListItemDto.builder()
                        .keyword(s.getKeyword())
                        .summary(s.getSummary())
                        .aiTip(s.getAiTip())
                        .postCount(s.getPostCount())
                        .modifiedAt(s.getModifiedAt())
                        .build())
                .toList();
    }

}
