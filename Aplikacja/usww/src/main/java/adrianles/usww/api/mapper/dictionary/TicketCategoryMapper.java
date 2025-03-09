package adrianles.usww.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.domain.entity.dictionary.TicketCategory;
import org.springframework.stereotype.Component;

@Component
public class TicketCategoryMapper extends AbstractDictionaryMapper<TicketCategory, TicketCategoryDTO> {

    @Override
    protected TicketCategoryDTO createDto(Integer id, String idn, String name) {
        return new TicketCategoryDTO(id, idn, name);
    }
}
