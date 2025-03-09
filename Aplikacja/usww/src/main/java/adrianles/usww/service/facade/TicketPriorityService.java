package adrianles.usww.service.facade;

import adrianles.usww.api.dto.TicketDTO;
import org.springframework.transaction.annotation.Transactional;

public interface TicketPriorityService {
    @Transactional
    TicketDTO updateTicketPriority(Integer ticketId, Integer priorityId);

    @Transactional
    TicketDTO setDefaultPriority(Integer ticketId);
}
