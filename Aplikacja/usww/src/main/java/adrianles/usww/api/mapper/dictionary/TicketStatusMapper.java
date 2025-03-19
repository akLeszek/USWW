package adrianles.usww.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.TicketStatusDTO;
import adrianles.usww.domain.entity.dictionary.TicketStatus;
import org.springframework.stereotype.Component;

@Component
public class TicketStatusMapper extends AbstractDictionaryMapper<TicketStatus, TicketStatusDTO> {

    @Override
    protected TicketStatusDTO createDto(Integer id, String idn, String name) {
        return new TicketStatusDTO(id, idn, name);
    }
}
