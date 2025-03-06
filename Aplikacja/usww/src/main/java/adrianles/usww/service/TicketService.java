package adrianles.usww.service;

import adrianles.usww.dto.TicketDTO;
import adrianles.usww.dto.TicketFilterCriteriaDTO;
import adrianles.usww.entity.Ticket;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.repository.TicketMessageRepository;
import adrianles.usww.repository.TicketRepository;
import adrianles.usww.repository.UserRepository;
import adrianles.usww.repository.dictionary.TicketCategoryRepository;
import adrianles.usww.repository.dictionary.TicketPriorityRepository;
import adrianles.usww.repository.dictionary.TicketStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TicketPriorityRepository ticketPriorityRepository;
    private final TicketMessageRepository ticketMessageRepository;

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<TicketDTO> getFilteredTickets(TicketFilterCriteriaDTO searchCriteria, Pageable pageable) {
        Specification<Ticket> specification = TicketSpecifications.buildSpecification(searchCriteria);
        return ticketRepository.findAll(specification, pageable).map(this::convertToDTO);
    }

    public List<TicketDTO> getActiveTickets() {
        return ticketRepository.findByArchiveFalse().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<TicketDTO> getArchivedTickets() {
        return ticketRepository.findByArchiveTrue().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public TicketDTO getTicketById(Integer id) {
        Ticket ticket = findTicketById(id);
        return convertToDTO(ticket);
    }

    private Ticket findTicketById(Integer id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    @Transactional
    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        setTicketFromTicketDTO(ticket, ticketDTO);
        ticket.setInsertedDate(LocalDateTime.now());
        ticket.setChangeDate(LocalDateTime.now());
        ticket.setLastActivityDate(LocalDateTime.now());
        ticket.setArchive(false);

        Ticket savedTicket = ticketRepository.save(ticket);
        return convertToDTO(savedTicket);
    }

    @Transactional
    public TicketDTO updateTicket(Integer ticketId, TicketDTO ticketDTO) {
        Ticket ticket = findTicketById(ticketId);
        setTicketFromTicketDTO(ticket, ticketDTO);
        ticket.setChangeDate(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);
        return convertToDTO(savedTicket);
    }

    @Transactional
    public TicketDTO updateTicketPriority(Integer ticketId, Integer priorityId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.setPriority(ticketPriorityRepository.findById(priorityId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found")));
        ticket.setChangeDate(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return convertToDTO(updatedTicket);
    }

    @Transactional
    public TicketDTO archiveTicket(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.setArchive(true);
        ticket.setChangeDate(LocalDateTime.now());
        archiveTicketMessages(ticketId);

        Ticket archivedTicket = ticketRepository.save(ticket);
        return convertToDTO(archivedTicket);
    }

    @Transactional
    public TicketDTO restoreTicket(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.setArchive(false);
        ticket.setChangeDate(LocalDateTime.now());
        restoreTicketMessages(ticketId);

        Ticket restoredTicket = ticketRepository.save(ticket);
        return convertToDTO(restoredTicket);
    }

    private void archiveTicketMessages(Integer ticketId) {
        archiveTicketMessages(ticketId, true);
    }

    private void restoreTicketMessages(Integer ticketId) {
        archiveTicketMessages(ticketId, false);
    }

    private void archiveTicketMessages(Integer ticketId, boolean archive) {
        ticketMessageRepository.findByTicketId(ticketId).forEach(ticketMessage -> {
            ticketMessage.setArchive(archive);
            ticketMessageRepository.save(ticketMessage);
        });
    }

    @Transactional
    public void updateLastActivityDate(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.setLastActivityDate(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    private void setTicketFromTicketDTO(Ticket ticket, TicketDTO ticketDTO) {
        ticket.setTitle(ticketDTO.getTitle());
        ticket.setArchive(ticketDTO.isArchive());

        if (ticketDTO.getOperatorId() != null) {
            ticket.setOperator(userRepository.findById(ticketDTO.getOperatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Operator not found")));
        }

        if (ticketDTO.getStudentId() != null) {
            ticket.setStudent(userRepository.findById(ticketDTO.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        }

        if (ticketDTO.getStatusId() != null) {
            ticket.setStatus(ticketStatusRepository.findById(ticketDTO.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found")));
        }

        if (ticketDTO.getCategoryId() != null) {
            ticket.setCategory(ticketCategoryRepository.findById(ticketDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }

        if (ticketDTO.getPriorityId() != null) {
            ticket.setPriority(ticketPriorityRepository.findById(ticketDTO.getPriorityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Priority not found")));
        } else if (ticket.getPriority() == null) {
            ticket.setPriority(ticketPriorityRepository.findByIdn("MEDIUM")
                    .orElseThrow(() -> new ResourceNotFoundException("Default priority not found")));
        }
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(ticket.getId());
        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setOperatorId(ticket.getOperator() != null ? ticket.getOperator().getId() : null);
        ticketDTO.setStudentId(ticket.getStudent() != null ? ticket.getStudent().getId() : null);
        ticketDTO.setCategoryId(ticket.getCategory() != null ? ticket.getCategory().getId() : null);
        ticketDTO.setPriorityId(ticket.getPriority() != null ? ticket.getPriority().getId() : null);
        ticketDTO.setInsertedDate(ticket.getInsertedDate() != null ? ticket.getInsertedDate().toString() : null);
        ticketDTO.setChangeDate(ticket.getChangeDate() != null ? ticket.getChangeDate().toString() : null);
        ticketDTO.setArchive(ticket.isArchive());
        return ticketDTO;
    }
}
