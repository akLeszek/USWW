package adrianles.usww.repository.dictionary;

import adrianles.usww.entity.dictionary.TicketPriority;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketPriorityRepository extends DictionaryRepository<TicketPriority> {
    Optional<TicketPriority> findByIdn(String idn);
}
