package adrianles.usww.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessageDTO {
    private Integer id;
    private String messageText;
    private String insertDate;
    private Integer ticketId;
    private Integer senderId;
}
