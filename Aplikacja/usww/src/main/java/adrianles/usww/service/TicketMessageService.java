package adrianles.usww.service;

import adrianles.usww.dto.TicketMessageDTO;
import adrianles.usww.entity.TicketMessage;
import adrianles.usww.repository.TicketMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketMessageService {

    private final TicketMessageRepository ticketMessageRepository;

    public List<TicketMessageDTO> getAllMessagesByTicketId(Integer ticketId) {
        return ticketMessageRepository.findByTicketId(ticketId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TicketMessageDTO createMessage(TicketMessageDTO messageDTO) {
        TicketMessage message = new TicketMessage();
        message.setMessageText(messageDTO.getMessageText());

        TicketMessage savedMessage = ticketMessageRepository.save(message);
        return convertToDTO(savedMessage);
    }

    private TicketMessageDTO convertToDTO(TicketMessage message) {
        return new TicketMessageDTO(
                message.getId(),
                message.getMessageText(),
                message.getInsertDate().toString(),
                message.getTicket().getId(),
                message.getSender().getId()
        );
    }
}
