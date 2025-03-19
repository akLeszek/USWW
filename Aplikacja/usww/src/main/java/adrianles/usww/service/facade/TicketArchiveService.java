package adrianles.usww.service.facade;

import adrianles.usww.api.dto.TicketDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TicketArchiveService {

    List<TicketDTO> getActiveTickets();

    List<TicketDTO> getArchivedTickets();

    @Transactional
    TicketDTO archiveTicket(Integer ticketId);

    @Transactional
    TicketDTO restoreTicket(Integer ticketId);

}
