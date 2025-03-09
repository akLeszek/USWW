package adrianles.usww.api.mapper;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.domain.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketDTO toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(ticket.getId());
        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setInsertedDate(ticket.getInsertedDate() != null ? ticket.getInsertedDate().toString() : null);
        ticketDTO.setChangeDate(ticket.getChangeDate() != null ? ticket.getChangeDate().toString() : null);
        ticketDTO.setArchive(ticket.isArchive());
        ticketDTO.setOperatorId(ticket.getOperator() != null ? ticket.getOperator().getId() : null);
        ticketDTO.setStudentId(ticket.getStudent() != null ? ticket.getStudent().getId() : null);
        ticketDTO.setStatusId(ticket.getStatus() != null ? ticket.getStatus().getId() : null);
        ticketDTO.setCategoryId(ticket.getCategory() != null ? ticket.getCategory().getId() : null);
        ticketDTO.setPriorityId(ticket.getPriority() != null ? ticket.getPriority().getId() : null);
        return ticketDTO;
    }

}
