package adrianles.usww.service.impl;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.mapper.TicketMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.repositiory.TicketRepository;
import adrianles.usww.domain.repositiory.dictionary.TicketPriorityRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.TicketPriorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketPriorityServiceImpl implements TicketPriorityService {
    private final TicketRepository ticketRepository;
    private final TicketPriorityRepository ticketPriorityRepository;
    private final TicketMapper ticketMapper;

    @Override
    public TicketDTO updateTicketPriority(Integer ticketId, Integer priorityId) {
        Ticket ticket = findTicketById(ticketId);

        ticket.setPriority(ticketPriorityRepository.findById(priorityId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found with id: " + priorityId)));
        ticket.setChangeDate(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public TicketDTO setDefaultPriority(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);

        ticket.setPriority(ticketPriorityRepository.findByIdn("MEDIUM")
                .orElseThrow(() -> new ResourceNotFoundException("Default priority not found")));
        ticket.setChangeDate(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    private Ticket findTicketById(Integer id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }
}
