package adrianles.usww.service.impl;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import adrianles.usww.api.mapper.TicketMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.repositiory.TicketRepository;
import adrianles.usww.domain.repositiory.UserRepository;
import adrianles.usww.domain.repositiory.dictionary.TicketCategoryRepository;
import adrianles.usww.domain.repositiory.dictionary.TicketPriorityRepository;
import adrianles.usww.domain.repositiory.dictionary.TicketStatusRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.TicketService;
import adrianles.usww.domain.specification.TicketSpecifications;
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
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final TicketStatusRepository ticketStatusRepository;
    private final TicketPriorityRepository ticketPriorityRepository;
    private final TicketMapper ticketMapper;

    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TicketDTO> getFilteredTickets(TicketFilterCriteriaDTO criteria, Pageable pageable) {
        Specification<Ticket> spec = TicketSpecifications.buildSpecification(criteria);
        return ticketRepository.findAll(spec, pageable).map(ticketMapper::toDto);
    }

    @Override
    public TicketDTO getTicketById(Integer id) {
        Ticket ticket = findTicketById(id);
        return ticketMapper.toDto(ticket);
    }

    @Override
    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        updateTicketFromDTO(ticket, ticketDTO);
        ticket.setInsertedDate(LocalDateTime.now());
        ticket.setChangeDate(LocalDateTime.now());
        ticket.setLastActivityDate(LocalDateTime.now());
        ticket.setArchive(false);

        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);
    }

    @Override
    @Transactional
    public TicketDTO updateTicket(Integer id, TicketDTO ticketDTO) {
        Ticket ticket = findTicketById(id);

        updateTicketFromDTO(ticket, ticketDTO);
        ticket.setChangeDate(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public void updateLastActivityDate(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.setLastActivityDate(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    protected Ticket findTicketById(Integer id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }


    protected void updateTicketFromDTO(Ticket ticket, TicketDTO ticketDTO) {
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
}
