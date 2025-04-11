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

    TicketDTO getTicketById(Integer ticketId);

    @Transactional
    TicketDTO createTicket(TicketDTO ticketDTO);

    @Transactional
    TicketDTO updateTicket(Integer ticketId, TicketDTO ticketDTO);

    Page<TicketDTO> getFilteredTickets(TicketFilterCriteriaDTO criteria, Pageable pageable);

    List<TicketDTO> getActiveTicketsForCurrentUser();

    List<TicketDTO> getArchivedTicketsForCurrentUser();

    @Transactional
    TicketDTO updateTicketStatus(Integer ticketId, Integer statusId);

    @Transactional
    TicketDTO updateTicketPriority(Integer ticketId, Integer priorityId);

    @Transactional
    TicketDTO archiveTicket(Integer ticketId);

    @Transactional
    TicketDTO restoreTicket(Integer ticketId);

    @Transactional
    void updateLastActivityDate(Integer ticketId);

    int countActiveTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator);

    int countClosedTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator);

    int countPendingTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator);

    int countAllTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator);

    List<TicketDTO> getRecentTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator, int limit);

    List<TicketDTO> getAllActiveTickets();

    List<TicketDTO> getAllArchivedTickets();

    List<TicketDTO> getUnassignedTickets();

    @Transactional
    TicketDTO assignTicketToOperator(Integer ticketId, Integer operatorId);
}
