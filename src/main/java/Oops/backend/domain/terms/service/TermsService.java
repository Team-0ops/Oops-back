package Oops.backend.domain.terms.service;

import Oops.backend.domain.terms.dto.response.TermsResponse;
import Oops.backend.domain.terms.entity.Terms;
import Oops.backend.domain.terms.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsService {

    private final TermsRepository termsRepository;

    public List<TermsResponse> getAllTerms() {
        List<Terms> all = termsRepository.findAllByOrderByIdAsc();
        return all.stream().map(TermsResponse::from).toList();
    }
}
