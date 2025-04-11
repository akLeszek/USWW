package adrianles.usww.api.controller.secured;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.security.authorization.AuthorizationService;
import adrianles.usww.service.facade.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/secured/tickets")
@RequiredArgsConstructor
public class SecuredTicketController {

    private final TicketService ticketService;
    private final AuthorizationService authorizationService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable int id) {
        if (!authorizationService.canAccessTicket(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak uprawnień do dostępu do tego zgłoszenia");
        }
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable int id, @RequestBody TicketDTO ticketDTO) {
        if (!authorizationService.canModifyTicket(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak uprawnień do modyfikacji tego zgłoszenia");
        }
        return ResponseEntity.ok(ticketService.updateTicket(id, ticketDTO));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<TicketDTO> archiveTicket(@PathVariable int id) {
        if (!authorizationService.canArchiveTicket(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak uprawnień do archiwizacji tego zgłoszenia");
        }
        return ResponseEntity.ok().build();
    }
}
