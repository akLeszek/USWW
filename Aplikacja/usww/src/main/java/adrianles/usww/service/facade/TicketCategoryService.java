package adrianles.usww.service.facade;

import adrianles.usww.api.dto.dictionary.TicketCategoryDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TicketCategoryService {

    List<TicketCategoryDTO> getAllTicketCategories();

    TicketCategoryDTO getTicketCategoryById(Integer id);

    TicketCategoryDTO getTicketCategoryByIdn(String idn);
}
