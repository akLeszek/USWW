package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.api.mapper.dictionary.TicketPriorityMapper;
import adrianles.usww.domain.entity.dictionary.TicketPriority;
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
        TicketPriority ticketPriority = ticketPriorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket priority with id " + id + " not found"));
        return ticketPriorityMapper.toDto(ticketPriority);
    }

    @Override
    public TicketPriorityDTO getTicketPriorityByIdn(String idn) {
        TicketPriority ticketPriority = ticketPriorityRepository.findByIdn(idn)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket priority with idn " + idn + " not found"));
        return ticketPriorityMapper.toDto(ticketPriority);
    }
}
