package adrianles.usww.controller;

import adrianles.usww.dto.TicketDTO;
import adrianles.usww.dto.TicketFilterCriteriaDTO;
import adrianles.usww.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TicketDTO>> getActiveTickets() {
        return ResponseEntity.ok(ticketService.getActiveTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody TicketDTO ticketDTO) {
        return ResponseEntity.ok(ticketService.createTicket(ticketDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable int id, @RequestBody TicketDTO ticketDTO) {
        return ResponseEntity.ok(ticketService.updateTicket(id, ticketDTO));
    }

    @PutMapping("/{id}/priority/{priorityId}")
    public ResponseEntity<TicketDTO> updateTicketPriority(@PathVariable int id, @PathVariable int priorityId) {
        return ResponseEntity.ok(ticketService.updateTicketPriority(id, priorityId));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<TicketDTO> archiveTicket(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.archiveTicket(id));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<TicketDTO> restoreTicket(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.restoreTicket(id));
    }

    @GetMapping("/search")
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
