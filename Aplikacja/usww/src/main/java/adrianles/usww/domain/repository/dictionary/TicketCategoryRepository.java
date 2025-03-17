package adrianles.usww.domain.repository.dictionary;

import adrianles.usww.domain.entity.dictionary.TicketCategory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketCategoryRepository extends DictionaryRepository<TicketCategory> {
    Optional<TicketCategory> findByIdn(String idn);
}
