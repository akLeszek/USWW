package adrianles.usww.controller;

import adrianles.usww.entity.Ticket;
import adrianles.usww.service.AbstractService;
import org.springframework.stereotype.Controller;

@Controller(value = "tickets")
public class TicketController extends AbstractController<Ticket> {
    public TicketController(AbstractService<Ticket> service) {
        super(service);
    }
}
