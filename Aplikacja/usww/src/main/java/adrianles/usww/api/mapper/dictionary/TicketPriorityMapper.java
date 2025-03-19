package adrianles.usww.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.domain.entity.dictionary.TicketPriority;
import org.springframework.stereotype.Component;

@Component
public class TicketPriorityMapper extends AbstractDictionaryMapper<TicketPriority, TicketPriorityDTO> {

    @Override
    protected TicketPriorityDTO createDto(Integer id, String idn, String name) {
        return new TicketPriorityDTO(id, idn, name);
    }
}
