package adrianles.usww.service.facade;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TicketService {
    List<TicketDTO> getAllTickets();

    Page<TicketDTO> getFilteredTickets(TicketFilterCriteriaDTO criteria, Pageable pageable);

    TicketDTO getTicketById(Integer ticketId);

    @Transactional
    TicketDTO createTicket(TicketDTO ticketDTO);

    @Transactional
    TicketDTO updateTicket(Integer ticketId, TicketDTO ticketDTO);

    @Transactional
    void updateLastActivityDate(Integer ticketId);
}
