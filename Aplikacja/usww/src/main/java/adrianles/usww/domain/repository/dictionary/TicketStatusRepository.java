package adrianles.usww.domain.repository.dictionary;

import adrianles.usww.domain.entity.dictionary.TicketStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketStatusRepository extends DictionaryRepository<TicketStatus> {
    Optional<TicketStatus> findByIdn(String idn);
}
