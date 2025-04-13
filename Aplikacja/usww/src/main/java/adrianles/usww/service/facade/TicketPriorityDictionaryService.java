package adrianles.usww.service.facade;

import adrianles.usww.api.dto.dictionary.TicketPriorityDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TicketPriorityDictionaryService {

    List<TicketPriorityDTO> getAllTicketPriorities();

    TicketPriorityDTO getTicketPriorityById(Integer id);

    TicketPriorityDTO getTicketPriorityByIdn(String idn);

    @Transactional
    TicketPriorityDTO createTicketPriority(TicketPriorityDTO priorityDTO);

    @Transactional
    TicketPriorityDTO updateTicketPriority(Integer id, TicketPriorityDTO priorityDTO);

    @Transactional
    void deleteTicketPriority(Integer id);
}
