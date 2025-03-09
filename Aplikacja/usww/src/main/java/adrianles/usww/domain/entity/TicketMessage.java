package adrianles.usww.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TICKET_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessage extends AbstractEntity {

    @NotNull(message = "Ticket cannot be null")
    @ManyToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_ticket_message_ticket"))
    private Ticket ticket;

    @NotNull(message = "Sender cannot be null")
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_ticket_message_sender"))
    private User sender;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String messageText;

    private LocalDateTime insertDate;

    @Column(nullable = false)
    private boolean archive = false;
}
