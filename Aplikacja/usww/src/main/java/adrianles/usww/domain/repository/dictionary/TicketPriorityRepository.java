package adrianles.usww.domain.repository.dictionary;

import adrianles.usww.domain.entity.dictionary.TicketPriority;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketPriorityRepository extends DictionaryRepository<TicketPriority> {
    Optional<TicketPriority> findByIdn(String idn);
}
