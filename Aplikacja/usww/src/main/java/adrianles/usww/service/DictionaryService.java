package adrianles.usww.service;

import adrianles.usww.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.dto.dictionary.TicketStatusDTO;
import adrianles.usww.repository.dictionary.TicketCategoryRepository;
import adrianles.usww.repository.dictionary.TicketPriorityRepository;
import adrianles.usww.repository.dictionary.TicketStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final TicketCategoryRepository ticketCategoryRepository;
    private final TicketStatusRepository ticketStatusRepository;
    private final TicketPriorityRepository ticketPriorityRepository;

    public List<TicketCategoryDTO> getAllTicketCategories() {
        return ticketCategoryRepository.findAll().stream()
                .map(category ->
                        new TicketCategoryDTO(category.getId(), category.getIdn(), category.getName()))
                .collect(Collectors.toList());
    }

    public List<TicketStatusDTO> getAllTicketStatuses() {
        return ticketStatusRepository.findAll().stream()
                .map(status ->
                        new TicketStatusDTO(status.getId(), status.getIdn(), status.getName()))
                .collect(Collectors.toList());
    }

    public List<TicketPriorityDTO> getAllTicketPriorities() {
        return ticketPriorityRepository.findAll().stream().
                map(priority ->
                        new TicketPriorityDTO(priority.getId(), priority.getIdn(), priority.getName()))
                .collect(Collectors.toList());
    }
}
