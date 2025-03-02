package adrianles.usww.service;

import adrianles.usww.dto.TicketMessageDTO;
import adrianles.usww.entity.TicketMessage;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.repository.TicketMessageRepository;
import adrianles.usww.repository.TicketRepository;
import adrianles.usww.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketMessageService {

    private final TicketMessageRepository ticketMessageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public List<TicketMessageDTO> getAllMessagesByTicketId(Integer ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new IllegalArgumentException("Ticket id " + ticketId + " does not exist");
        }

        return ticketMessageRepository.findByTicketId(ticketId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TicketMessageDTO createMessage(TicketMessageDTO messageDTO) {
        TicketMessage message = new TicketMessage();
        message.setMessageText(messageDTO.getMessageText());
        message.setInsertDate(LocalDateTime.now());

        message.setTicket(ticketRepository.findById(messageDTO.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid ticketId")));

        message.setSender(userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid senderId")));

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
