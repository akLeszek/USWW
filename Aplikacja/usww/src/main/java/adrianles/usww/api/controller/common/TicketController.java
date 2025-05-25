package adrianles.usww.api.controller.common;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import adrianles.usww.service.facade.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TicketDTO>> getActiveTickets() {
        return ResponseEntity.ok(ticketService.getActiveTicketsForCurrentUser());
    }

    @GetMapping("/archive")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TicketDTO>> getArchiveTickets() {
        return ResponseEntity.ok(ticketService.getArchivedTicketsForCurrentUser());
    }

    @GetMapping("/admin/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<TicketDTO>> getAllActiveTickets() {
        return ResponseEntity.ok(ticketService.getAllActiveTickets());
    }

    @GetMapping("/admin/archive")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<TicketDTO>> getAllArchivedTickets() {
        return ResponseEntity.ok(ticketService.getAllArchivedTickets());
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<TicketDTO>> getUnassignedTickets() {
        return ResponseEntity.ok(ticketService.getUnassignedTickets());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Ticket', 'READ')")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody TicketDTO ticketDTO) {
        return ResponseEntity.ok(ticketService.createTicket(ticketDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Ticket', 'WRITE')")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable int id, @RequestBody TicketDTO ticketDTO) {
        return ResponseEntity.ok(ticketService.updateTicket(id, ticketDTO));
    }

    @PutMapping("/{id}/status/{statusId}")
    @PreAuthorize("hasPermission(#id, 'Ticket', 'WRITE') and hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<TicketDTO> updateTicketStatus(@PathVariable int id, @PathVariable int statusId) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, statusId));
    }

    @PutMapping("/{id}/priority/{priorityId}")
    @PreAuthorize("hasPermission(#id, 'Ticket', 'WRITE') and hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<TicketDTO> updateTicketPriority(@PathVariable int id, @PathVariable int priorityId) {
        return ResponseEntity.ok(ticketService.updateTicketPriority(id, priorityId));
    }

    @PutMapping("/{id}/assign/{operatorId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<TicketDTO> assignTicketToOperator(@PathVariable int id, @PathVariable int operatorId) {
        return ResponseEntity.ok(ticketService.assignTicketToOperator(id, operatorId));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasPermission(#id, 'Ticket', 'ARCHIVE')")
    public ResponseEntity<TicketDTO> archiveTicket(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.archiveTicket(id));
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasPermission(#id, 'Ticket', 'ARCHIVE') and hasAuthority('ADMIN')")
    public ResponseEntity<TicketDTO> restoreTicket(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.restoreTicket(id));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TicketDTO>> searchTickets(
            @ModelAttribute TicketFilterCriteriaDTO criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        Sort.Direction direction = order.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, direction, sortBy, order);

        criteria.setSortBy(sortBy);
        criteria.setSortOrder(order);

        return ResponseEntity.ok(ticketService.getFilteredTickets(criteria, pageable));
    }
}
