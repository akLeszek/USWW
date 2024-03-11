package adrianles.usww.controller;

import adrianles.usww.entity.TicketMessage;
import adrianles.usww.service.AbstractService;
import org.springframework.stereotype.Controller;

@Controller(value = "/messages")
public class TicketMessageController extends AbstractController<TicketMessage> {
    public TicketMessageController(AbstractService<TicketMessage> service) {
        super(service);
    }
}
