package adrianles.usww.domain.repository;

import adrianles.usww.domain.entity.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Integer> {
    List<TicketMessage> findByTicketId(Integer ticketId);
}
