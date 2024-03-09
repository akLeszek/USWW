package adrianles.usww.entity.dictionary;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TICKET_STATUS")
public class TicketStatus extends Dictionary {

    public TicketStatus() {
    }

    public TicketStatus(Integer id, String idn, String name) {
        super(id, idn, name);
    }
}
