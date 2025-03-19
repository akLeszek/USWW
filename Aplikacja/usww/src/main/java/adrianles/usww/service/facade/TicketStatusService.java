package adrianles.usww.service.facade;

import adrianles.usww.api.dto.dictionary.TicketStatusDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TicketStatusService {

    List<TicketStatusDTO> getAllTicketStatuses();

    TicketStatusDTO getTicketStatusById(Integer id);

    TicketStatusDTO getTicketStatusByIdn(String idn);
}
