package adrianles.usww.repository;

import adrianles.usww.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByStatusId(Integer id);

    List<Ticket> findByOperatorId(Integer id);
}
