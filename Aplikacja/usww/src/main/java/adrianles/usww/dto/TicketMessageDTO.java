package adrianles.usww.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessageDTO {
    private Integer id;
    private String messageText;
    private String insertDate;
    @NotNull(message = "Ticket cannot be null")
    private Integer ticketId;
    @NotNull(message = "Sender cannot be null")
    private Integer senderId;
}
