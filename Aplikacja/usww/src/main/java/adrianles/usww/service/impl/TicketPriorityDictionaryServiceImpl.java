package adrianles.usww.service.impl;

import adrianles.usww.api.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.api.mapper.dictionary.TicketPriorityMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.dictionary.TicketPriority;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.dictionary.TicketPriorityRepository;
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
    private final TicketRepository ticketRepository;

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

    @Override
    public TicketPriorityDTO createTicketPriority(TicketPriorityDTO priorityDTO) {
        if (ticketPriorityRepository.findByIdn(priorityDTO.getIdn()).isPresent()) {
            throw new IllegalArgumentException("Ticket priority with idn " + priorityDTO.getIdn() + " already exists");
        }

        TicketPriority ticketPriority = new TicketPriority();
        ticketPriority.setIdn(priorityDTO.getIdn());
        ticketPriority.setName(priorityDTO.getName());

        TicketPriority savedPriority = ticketPriorityRepository.save(ticketPriority);
        return ticketPriorityMapper.toDto(savedPriority);
    }

    @Override
    public TicketPriorityDTO updateTicketPriority(Integer id, TicketPriorityDTO priorityDTO) {
        TicketPriority existingPriority = ticketPriorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket priority with id " + id + " not found"));

        if (!existingPriority.getIdn().equals(priorityDTO.getIdn()) &&
                ticketPriorityRepository.findByIdn(priorityDTO.getIdn()).isPresent()) {
            throw new IllegalArgumentException("Ticket priority with idn " + priorityDTO.getIdn() + " already exists");
        }

        existingPriority.setIdn(priorityDTO.getIdn());
        existingPriority.setName(priorityDTO.getName());

        TicketPriority updatedPriority = ticketPriorityRepository.save(existingPriority);
        return ticketPriorityMapper.toDto(updatedPriority);
    }

    @Override
    public void deleteTicketPriority(Integer id) {
        if (!ticketPriorityRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket priority with id " + id + " not found");
        }

        List<Ticket> ticketsWithPriority = ticketRepository.findByPriorityId(id);
        if (!ticketsWithPriority.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete priority that is used by tickets");
        }

        ticketPriorityRepository.deleteById(id);
    }
}
