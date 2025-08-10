package Oops.backend.domain.terms.dto.response;

import Oops.backend.domain.terms.entity.RequiredType;
import Oops.backend.domain.terms.entity.Terms;

public record TermsResponse(
        Long id,
        String title,
        String content,
        RequiredType required
) {
    public static TermsResponse from(Terms t) {
        return new TermsResponse(t.getId(), t.getTitle(), t.getContent(), t.getRequired());
    }
}
