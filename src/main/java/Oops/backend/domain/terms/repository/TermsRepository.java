package Oops.backend.domain.terms.repository;

import Oops.backend.domain.terms.entity.RequiredType;
import Oops.backend.domain.terms.entity.Terms;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TermsRepository extends CrudRepository<Terms, Long> {
    List<Terms> findAllByRequired(RequiredType required);

}
