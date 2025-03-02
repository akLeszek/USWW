package adrianles.usww.service;

import adrianles.usww.dto.TicketDTO;
import adrianles.usww.entity.Ticket;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.repository.TicketRepository;
import adrianles.usww.repository.UserRepository;
import adrianles.usww.repository.dictionary.TicketCategoryRepository;
import adrianles.usww.repository.dictionary.TicketStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final TicketStatusRepository ticketStatusRepository;

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO getTicketById(Integer id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return convertToDTO(ticket);
    }

    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        ticket.setTitle(ticketDTO.getTitle());
        ticket.setInsertedDate(LocalDateTime.now());
        ticket.setChangeDate(LocalDateTime.now());
        ticket.setArchive(ticketDTO.isArchive());

        ticket.setOperator(userRepository.findById(ticketDTO.getOperatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found")));

        ticket.setStudent(userRepository.findById(ticketDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found")));

        ticket.setStatus(ticketStatusRepository.findById(ticketDTO.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found")));

        ticket.setCategory(ticketCategoryRepository.findById(ticketDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found")));

        Ticket savedTicket = ticketRepository.save(ticket);
        return convertToDTO(savedTicket);
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        return new TicketDTO(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getInsertedDate() != null ? ticket.getInsertedDate().toString() : null,
                ticket.getChangeDate() != null ? ticket.getChangeDate().toString() : null,
                ticket.isArchive(),
                ticket.getOperator() != null ? ticket.getOperator().getId() : null,
                ticket.getStudent() != null ? ticket.getStudent().getId() : null,
                ticket.getStatus() != null ? ticket.getStatus().getId() : null,
                ticket.getCategory() != null ? ticket.getCategory().getId() : null
        );
    }
}
