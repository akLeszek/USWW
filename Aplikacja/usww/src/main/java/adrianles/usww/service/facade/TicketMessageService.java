package adrianles.usww.service.facade;

import adrianles.usww.api.dto.TicketMessageDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TicketMessageService {

    List<TicketMessageDTO> getAllMessagesByTicketId(Integer ticketId);

    @Transactional
    TicketMessageDTO createMessage(TicketMessageDTO messageDTO);

    @Transactional
    TicketMessageDTO archiveMessage(Integer messageId);

    @Transactional
    void archiveMessageByTicketId(Integer ticketId);
}
