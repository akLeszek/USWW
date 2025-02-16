package adrianles.usww.controller;

import adrianles.usww.dto.TicketMessageDTO;
import adrianles.usww.service.TicketMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class TicketMessageController {
    private final TicketMessageService ticketMessageService;

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketMessageDTO>> getTicketMessage(@PathVariable("ticketId") int ticketId) {
        return ResponseEntity.ok(ticketMessageService.getAllMessagesByTicketId(ticketId));
    }

    @PostMapping
    public ResponseEntity<TicketMessageDTO> addTicketMessage(@RequestBody TicketMessageDTO ticketMessageDTO) {
        return ResponseEntity.ok(ticketMessageService.createMessage(ticketMessageDTO));
    }
}
