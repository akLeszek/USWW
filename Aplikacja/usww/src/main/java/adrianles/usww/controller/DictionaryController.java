package adrianles.usww.controller;

import adrianles.usww.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.dto.dictionary.TicketStatusDTO;
import adrianles.usww.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService dictionaryService;

    @GetMapping("/categories")
    public ResponseEntity<List<TicketCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(dictionaryService.getAllTicketCategories());
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<TicketStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(dictionaryService.getAllTicketStatuses());
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<TicketPriorityDTO>> getAllPriorities() {
        return ResponseEntity.ok(dictionaryService.getAllTicketPriorities());
    }
}
