package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.api.mapper.dictionary.TicketPriorityMapper;
import adrianles.usww.domain.repositiory.dictionary.TicketPriorityRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.TicketPriorityDictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketPriorityDictionaryServiceImpl implements TicketPriorityDictionaryService {
    private final TicketPriorityDictionaryService ticketPriorityDictionaryService;
    private final TicketPriorityRepository ticketPriorityRepository;
    private final TicketPriorityMapper ticketPriorityMapper;

    @Override
    public List<TicketPriorityDTO> getAllTicketPriorities() {
        return ticketPriorityRepository.findAll().stream()
                .map(ticketPriorityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketPriorityDTO getTicketPriorityById(Integer id) {
        return ticketPriorityRepository.findById(id)
                .map(ticketPriorityMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket priority with id " + id + " not found"));
    }

    @Override
    public TicketPriorityDTO getTicketPriorityByIdn(String idn) {
        return ticketPriorityRepository.findByIdn(idn)
                .map(ticketPriorityMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket priority with idn " + idn + " not found"));
    }
}
