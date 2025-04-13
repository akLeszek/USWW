package adrianles.usww.api.controller.admin;

import adrianles.usww.api.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.api.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.api.dto.dictionary.TicketStatusDTO;
import adrianles.usww.service.facade.TicketCategoryService;
import adrianles.usww.service.facade.TicketPriorityDictionaryService;
import adrianles.usww.service.facade.TicketStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dictionaries")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminDictionaryController {

    private final TicketCategoryService ticketCategoryService;
    private final TicketStatusService ticketStatusService;
    private final TicketPriorityDictionaryService ticketPriorityDictionaryService;

    // Category endpoints
    @PostMapping("/categories")
    public ResponseEntity<TicketCategoryDTO> createCategory(@Valid @RequestBody TicketCategoryDTO categoryDTO) {
        return ResponseEntity.ok(ticketCategoryService.createTicketCategory(categoryDTO));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<TicketCategoryDTO> updateCategory(@PathVariable Integer id, @Valid @RequestBody TicketCategoryDTO categoryDTO) {
        return ResponseEntity.ok(ticketCategoryService.updateTicketCategory(id, categoryDTO));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        ticketCategoryService.deleteTicketCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Status endpoints
    @PostMapping("/statuses")
    public ResponseEntity<TicketStatusDTO> createStatus(@Valid @RequestBody TicketStatusDTO statusDTO) {
        return ResponseEntity.ok(ticketStatusService.createTicketStatus(statusDTO));
    }

    @PutMapping("/statuses/{id}")
    public ResponseEntity<TicketStatusDTO> updateStatus(@PathVariable Integer id, @Valid @RequestBody TicketStatusDTO statusDTO) {
        return ResponseEntity.ok(ticketStatusService.updateTicketStatus(id, statusDTO));
    }

    @DeleteMapping("/statuses/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Integer id) {
        ticketStatusService.deleteTicketStatus(id);
        return ResponseEntity.noContent().build();
    }

    // Priority endpoints
    @PostMapping("/priorities")
    public ResponseEntity<TicketPriorityDTO> createPriority(@Valid @RequestBody TicketPriorityDTO priorityDTO) {
        return ResponseEntity.ok(ticketPriorityDictionaryService.createTicketPriority(priorityDTO));
    }

    @PutMapping("/priorities/{id}")
    public ResponseEntity<TicketPriorityDTO> updatePriority(@PathVariable Integer id, @Valid @RequestBody TicketPriorityDTO priorityDTO) {
        return ResponseEntity.ok(ticketPriorityDictionaryService.updateTicketPriority(id, priorityDTO));
    }

    @DeleteMapping("/priorities/{id}")
    public ResponseEntity<Void> deletePriority(@PathVariable Integer id) {
        ticketPriorityDictionaryService.deleteTicketPriority(id);
        return ResponseEntity.noContent().build();
    }
}
