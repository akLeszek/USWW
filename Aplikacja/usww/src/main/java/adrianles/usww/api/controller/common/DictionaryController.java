package adrianles.usww.api.controller.common;

import adrianles.usww.api.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.api.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.api.dto.dictionary.TicketStatusDTO;
import adrianles.usww.service.facade.TicketCategoryService;
import adrianles.usww.service.facade.TicketPriorityDictionaryService;
import adrianles.usww.service.facade.TicketStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final TicketCategoryService ticketCategoryService;
    private final TicketStatusService ticketStatusService;
    private final TicketPriorityDictionaryService ticketPriorityDictionaryService;

    @GetMapping("/categories")
    public ResponseEntity<List<TicketCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAllTicketCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<TicketCategoryDTO> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategoryById(id));
    }

    @GetMapping("/categories/idn/{idn}")
    public ResponseEntity<TicketCategoryDTO> getCategoryByIdn(@PathVariable String idn) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategoryByIdn(idn));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<TicketStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(ticketStatusService.getAllTicketStatuses());
    }

    @GetMapping("/statuses/{id}")
    public ResponseEntity<TicketStatusDTO> getTicketStatusById(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketStatusService.getTicketStatusById(id));
    }

    @GetMapping("/statuses/idn/{idn}")
    public ResponseEntity<TicketStatusDTO> getTicketStatusByIdn(@PathVariable String idn) {
        return ResponseEntity.ok(ticketStatusService.getTicketStatusByIdn(idn));
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<TicketPriorityDTO>> getAllPriorities() {
        return ResponseEntity.ok(ticketPriorityDictionaryService.getAllTicketPriorities());
    }

    @GetMapping("/priorities/{id}")
    public ResponseEntity<TicketPriorityDTO> getTicketPriorityById(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketPriorityDictionaryService.getTicketPriorityById(id));
    }

    @GetMapping("/priorities/idn/{idn}")
    public ResponseEntity<TicketPriorityDTO> getTicketPriorityByIdn(@PathVariable String idn) {
        return ResponseEntity.ok(ticketPriorityDictionaryService.getTicketPriorityByIdn(idn));
    }
}
