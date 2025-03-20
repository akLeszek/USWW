package adrianles.usww.service.impl;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.mapper.TicketMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.repository.TicketMessageRepository;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.TicketArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketArchiveServiceImpl implements TicketArchiveService {
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final TicketMessageRepository ticketMessageRepository;

    @Override
    public List<TicketDTO> getActiveTickets() {
        return ticketRepository.findByArchiveFalse().stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getArchivedTickets() {
        return ticketRepository.findByArchiveTrue().stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDTO archiveTicket(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.setArchive(true);
        ticket.setChangeDate(LocalDateTime.now());
        archiveTicketMessages(ticketId, true);

        Ticket archivedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(archivedTicket);
    }

    @Override
    public TicketDTO restoreTicket(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.setArchive(false);
        ticket.setChangeDate(LocalDateTime.now());
        archiveTicketMessages(ticketId, false);

        Ticket restoredTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(restoredTicket);
    }


    private Ticket findTicketById(Integer id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    private void archiveTicketMessages(Integer ticketId, boolean archive) {
        List<TicketMessage> messages = ticketMessageRepository.findByTicketId(ticketId);
        messages.forEach(message -> {
            message.setArchive(archive);
            ticketMessageRepository.save(message);
        });
    }
}
