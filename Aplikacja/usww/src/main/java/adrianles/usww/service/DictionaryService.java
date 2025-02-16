package adrianles.usww.service;

import adrianles.usww.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.repository.dictionary.TicketCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final TicketCategoryRepository ticketCategoryRepository;

    public List<TicketCategoryDTO> getAllTicketCategories() {
        return ticketCategoryRepository.findAll().stream()
                .map(category ->
                        new TicketCategoryDTO(category.getId(), category.getIdn(), category.getName()))
                .collect(Collectors.toList());
    }
}
