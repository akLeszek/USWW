package adrianles.usww.service.impl;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import adrianles.usww.api.mapper.TicketMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.repository.TicketMessageRepository;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.domain.repository.dictionary.TicketCategoryRepository;
import adrianles.usww.domain.repository.dictionary.TicketPriorityRepository;
import adrianles.usww.domain.repository.dictionary.TicketStatusRepository;
import adrianles.usww.domain.specification.TicketSpecifications;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.exception.UnauthorizedAccessException;
import adrianles.usww.security.authorization.AuthorizationService;
import adrianles.usww.security.userdetails.ExtendedUserDetails;
import adrianles.usww.service.facade.TicketService;
import adrianles.usww.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final TicketMessageRepository ticketMessageRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final TicketStatusRepository ticketStatusRepository;
    private final TicketPriorityRepository ticketPriorityRepository;
    private final TicketMapper ticketMapper;
    private final AuthorizationService authorizationService;

    @Override
    public List<TicketDTO> getAllTickets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Specification<Ticket> spec = TicketSpecifications.accessibleByUser(userId, roles);
        List<Ticket> tickets = ticketRepository.findAll(spec);

        return tickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TicketDTO> getFilteredTickets(TicketFilterCriteriaDTO criteria, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Specification<Ticket> spec = TicketSpecifications.buildSpecification(criteria);
        spec = spec.and(TicketSpecifications.accessibleByUser(userId, roles));

        return ticketRepository.findAll(spec, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    public TicketDTO getTicketById(Integer ticketId) {
        checkGetTicketAccess(ticketId);
        Ticket ticket = findTicketById(ticketId);
        return ticketMapper.toDto(ticket);
    }

    private void checkGetTicketAccess(Integer ticketId) {
        if (!authorizationService.canAccessTicket(ticketId)) {
            throw new UnauthorizedAccessException("Unauthorized access to ticket");
        }
    }

    @Override
    @Transactional
    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();

        if (userDetails.isStudent() && !userDetails.getUserId().equals(ticketDTO.getStudentId())) {
            throw new UnauthorizedAccessException("Unauthorized access to create ticket");
        }

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
    public TicketDTO updateTicket(Integer ticketId, TicketDTO ticketDTO) {
        Ticket ticket = findTicketById(ticketId);
        checkUpdateTicketAccess(ticketId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();

        if (userDetails.isAdmin()) {
            updateTicketFromDTO(ticket, ticketDTO);
        } else if (userDetails.isOperator()) {
            if (ticketDTO.getStatusId() != null) {
                ticket.setStatus(ticketStatusRepository.findById(ticketDTO.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Status not found")));
            }

            if (ticketDTO.getPriorityId() != null) {
                ticket.setPriority(ticketPriorityRepository.findById(ticketDTO.getPriorityId())
                        .orElseThrow(() -> new ResourceNotFoundException("Priority not found")));
            }

            if (ticketDTO.getCategoryId() != null) {
                ticket.setCategory(ticketCategoryRepository.findById(ticketDTO.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
            }
        } else if (userDetails.isStudent()) {
            if (ticketDTO.getTitle() != null) {
                ticket.setTitle(ticketDTO.getTitle());
            }
        }

        ticket.setChangeDate(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    private void checkUpdateTicketAccess(Integer ticketId) {
        if (!authorizationService.canModifyTicket(ticketId)) {
            throw new UnauthorizedAccessException("Unauthorized access to update ticket");
        }
    }

    @Override
    @Transactional
    public TicketDTO updateTicketStatus(Integer ticketId, Integer statusId) {
        Ticket ticket = findTicketById(ticketId);
        checkUpdateTicketAccess(ticketId);

        ticket.setStatus(ticketStatusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found with id: " + statusId)));
        ticket.setChangeDate(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    @Transactional
    public TicketDTO updateTicketPriority(Integer ticketId, Integer priorityId) {
        Ticket ticket = findTicketById(ticketId);
        checkUpdateTicketAccess(ticketId);

        ticket.setPriority(ticketPriorityRepository.findById(priorityId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found with id: " + priorityId)));
        ticket.setChangeDate(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public List<TicketDTO> getActiveTicketsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        Specification<Ticket> spec = Specification.where(TicketSpecifications.hasArchiveStatus(false));
        List<String> roles = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        spec = spec.and(TicketSpecifications.accessibleByUser(userId, roles));

        return ticketRepository.findAll(spec).stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getArchivedTicketsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExtendedUserDetails userDetails = (ExtendedUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        Specification<Ticket> spec = Specification.where(TicketSpecifications.hasArchiveStatus(true));
        List<String> roles = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        spec = spec.and(TicketSpecifications.accessibleByUser(userId, roles));

        return ticketRepository.findAll(spec).stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getAllActiveTickets() {
        if (!authorizationService.hasRole("ADMIN")) {
            throw new UnauthorizedAccessException("Only administrators can access all tickets");
        }

        return ticketRepository.findByArchiveFalse().stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getAllArchivedTickets() {
        if (!authorizationService.hasRole("ADMIN")) {
            throw new UnauthorizedAccessException("Only administrators can access all archive tickets");
        }

        return ticketRepository.findByArchiveTrue().stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TicketDTO archiveTicket(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        checkArchiveTicketAccess(ticketId);

        ticket.setArchive(true);
        ticket.setChangeDate(LocalDateTime.now());
        archiveTicketMessages(ticketId, true);

        Ticket archivedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(archivedTicket);
    }

    private void checkArchiveTicketAccess(Integer ticketId) {
        if (!authorizationService.canArchiveTicket(ticketId)) {
            throw new UnauthorizedAccessException("Unauthorized access to archive ticket");
        }
    }

    @Override
    @Transactional
    public TicketDTO restoreTicket(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        checkRestoreTicketAccess(ticketId);

        ticket.setArchive(false);
        ticket.setChangeDate(LocalDateTime.now());
        archiveTicketMessages(ticketId, false);

        Ticket restoredTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(restoredTicket);
    }

    private void checkRestoreTicketAccess(Integer ticketId) {
        if (!authorizationService.canArchiveTicket(ticketId)) {
            throw new UnauthorizedAccessException("Unauthorized access to restore ticket");
        }
    }

    @Override
    @Transactional
    public void updateLastActivityDate(Integer ticketId) {
        Ticket ticket = findTicketById(ticketId);
        checkUpdateTicketAccess(ticketId);

        ticket.setLastActivityDate(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    @Override
    public List<TicketDTO> getUnassignedTickets() {
        if (!authorizationService.hasRole("ADMIN", "OPERATOR")) {
            throw new UnauthorizedAccessException("Unauthorized access to unassigned tickets");
        }

        return ticketRepository.findByOperatorIsNullOrOperatorLogin(Constants.DEFAULT_OPERATOR_LOGIN).stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TicketDTO assignTicketToOperator(Integer ticketId, Integer operatorId) {
        Ticket ticket = findTicketById(ticketId);

        if (!authorizationService.hasRole("ADMIN") &&
                !authorizationService.canAccessTicket(ticketId)) {
            throw new UnauthorizedAccessException("Unauthorized access to assign this ticket");
        }

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with id: " + operatorId));

        if (!authorizationService.hasRole("ADMIN") &&
                !operator.getUserGroup().getIdn().equals("OPERATOR")) {
            throw new IllegalArgumentException("Ticket can be assigned only to operators");
        }

        if (!authorizationService.hasRole("ADMIN") &&
                operator.getOrganizationUnit() != null &&
                ticket.getStudent().getOrganizationUnit() != null &&
                !operator.getOrganizationUnit().getId().equals(ticket.getStudent().getOrganizationUnit().getId())) {
            throw new IllegalArgumentException("Operator must be from the same organization unit as the student");
        }

        ticket.setOperator(operator);
        ticket.setChangeDate(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public int countActiveTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator) {
        if (isAdmin) {
            return ticketRepository.countByArchiveFalseAndStatusIdNot(getClosedStatusId());
        } else if (isOperator) {
            return ticketRepository.countByOperatorIdAndArchiveFalseAndStatusIdNot(userId, getClosedStatusId());
        } else {
            return ticketRepository.countByStudentIdAndArchiveFalseAndStatusIdNot(userId, getClosedStatusId());
        }
    }

    @Override
    public int countClosedTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator) {
        if (isAdmin) {
            return ticketRepository.countByStatusId(getClosedStatusId());
        } else if (isOperator) {
            return ticketRepository.countByOperatorIdAndStatusId(userId, getClosedStatusId());
        } else {
            return ticketRepository.countByStudentIdAndStatusId(userId, getClosedStatusId());
        }
    }

    @Override
    public int countPendingTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator) {
        if (isAdmin) {
            return ticketRepository.countByStatusId(getNewStatusId());
        } else if (isOperator) {
            return ticketRepository.countByOperatorIdAndStatusId(userId, getNewStatusId());
        } else {
            return ticketRepository.countByStudentIdAndStatusId(userId, getNewStatusId());
        }
    }

    @Override
    public int countAllTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator) {
        if (isAdmin) {
            return (int) ticketRepository.count();
        } else if (isOperator) {
            return ticketRepository.countByOperatorId(userId);
        } else {
            return ticketRepository.countByStudentId(userId);
        }
    }

    @Override
    public List<TicketDTO> getRecentTicketsByUser(Integer userId, boolean isAdmin, boolean isOperator, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastActivityDate"));

        Page<Ticket> recentTickets;
        if (isAdmin) {
            recentTickets = ticketRepository.findAll(pageable);
        } else if (isOperator) {
            recentTickets = ticketRepository.findByOperatorId(userId, pageable);
        } else {
            recentTickets = ticketRepository.findByStudentId(userId, pageable);
        }

        return recentTickets.stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    protected void updateTicketFromDTO(Ticket ticket, TicketDTO ticketDTO) {
        ticket.setTitle(ticketDTO.getTitle());
        ticket.setArchive(ticketDTO.isArchive());

        if (ticketDTO.getOperatorId() != null) {
            ticket.setOperator(userRepository.findById(ticketDTO.getOperatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Operator not found")));
        } else {
            ticket.setOperator(userRepository.findByLogin(Constants.DEFAULT_OPERATOR_LOGIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Default operator not found")));
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

    protected Ticket findTicketById(Integer id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    private Integer getClosedStatusId() {
        return ticketStatusRepository.findByIdn("CLOSED")
                .orElseThrow(() -> new ResourceNotFoundException("Status CLOSED not found"))
                .getId();
    }

    private Integer getNewStatusId() {
        return ticketStatusRepository.findByIdn("NEW")
                .orElseThrow(() -> new ResourceNotFoundException("Status NEW not found"))
                .getId();
    }

    private void archiveTicketMessages(Integer ticketId, boolean archive) {
        List<TicketMessage> messages = ticketMessageRepository.findByTicketId(ticketId);
        messages.forEach(message -> {
            message.setArchive(archive);
            ticketMessageRepository.save(message);
        });
    }
}
