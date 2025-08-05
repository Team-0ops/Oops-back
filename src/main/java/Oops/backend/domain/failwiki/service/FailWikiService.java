package Oops.backend.domain.failwiki.service;

import Oops.backend.domain.failwiki.dto.FailWikiSummaryResponse;

public interface FailWikiService {
    FailWikiSummaryResponse getSummary(String keyword);
}