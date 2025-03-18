package adrianles.usww.domain.repositiory.dictionary;

import adrianles.usww.domain.entity.dictionary.OrganizationUnit;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationUnitRepository extends DictionaryRepository<OrganizationUnit> {
    Optional<OrganizationUnit> findByIdn(String idn);
}
