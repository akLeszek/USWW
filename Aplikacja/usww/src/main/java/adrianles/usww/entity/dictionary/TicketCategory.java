package adrianles.usww.entity.dictionary;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TICKET_CATEGORY")
public class TicketCategory extends Dictionary {
    public TicketCategory() {
    }

    public TicketCategory(String idn, String name) {
        super(idn, name);
    }

}
