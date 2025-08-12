package Oops.backend.domain.failwiki.service;

import Oops.backend.domain.failwiki.dto.FailWikiListItemDto;


import java.util.List;

public interface FailWikiQueryService {
    // 최신순으로 DB 저장본 전체 조회 (content만)
    List<FailWikiListItemDto> listAllLatest();
}