package adrianles.usww.domain.repository.dictionary;

import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import adrianles.usww.domain.repository.dictionary.DictionaryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationUnitRepository extends DictionaryRepository<OrganizationUnit> {
    Optional<OrganizationUnit> findByIdn(String idn);
}
