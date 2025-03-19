package adrianles.usww.api.mapper;

import adrianles.usww.api.dto.TicketMessageDTO;
import adrianles.usww.domain.entity.TicketMessage;
import org.springframework.stereotype.Component;

@Component
public class TicketMessageMapper {

    public TicketMessageDTO toDto(TicketMessage ticketMessage) {
        if (ticketMessage == null) {
            return null;
        }

        TicketMessageDTO ticketMessageDto = new TicketMessageDTO();
        ticketMessageDto.setId(ticketMessage.getId());
        ticketMessageDto.setMessageText(ticketMessage.getMessageText());
        ticketMessageDto.setInsertDate(ticketMessage.getInsertDate().toString());
        ticketMessageDto.setTicketId(ticketMessage.getTicket().getId());
        ticketMessageDto.setSenderId(ticketMessage.getSender().getId());
        return ticketMessageDto;
    }
}
