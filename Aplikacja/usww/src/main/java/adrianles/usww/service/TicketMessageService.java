package adrianles.usww.service;

import adrianles.usww.dto.TicketMessageDTO;
import adrianles.usww.entity.TicketMessage;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.repository.TicketMessageRepository;
import adrianles.usww.repository.TicketRepository;
import adrianles.usww.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketMessageService {

    private final TicketMessageRepository ticketMessageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;

    public List<TicketMessageDTO> getAllMessagesByTicketId(Integer ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new IllegalArgumentException("Ticket id " + ticketId + " does not exist");
        }

        return ticketMessageRepository.findByTicketId(ticketId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketMessageDTO createMessage(TicketMessageDTO messageDTO) {
        TicketMessage message = new TicketMessage();
        message.setMessageText(messageDTO.getMessageText());
        message.setInsertDate(LocalDateTime.now());
        message.setArchive(false);

        message.setTicket(ticketRepository.findById(messageDTO.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid ticketId")));

        message.setSender(userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid senderId")));

        TicketMessage savedMessage = ticketMessageRepository.save(message);
        ticketService.updateLastActivityDate(messageDTO.getTicketId());
        return convertToDTO(savedMessage);
    }

    @Transactional
    public TicketMessageDTO archiveMessage(Integer messageId) {
        TicketMessage ticketMessage = findTicketMessageById(messageId);
        ticketMessage.setArchive(true);
        TicketMessage archivedMessage = ticketMessageRepository.save(ticketMessage);
        return convertToDTO(archivedMessage);
    }

    private TicketMessage findTicketMessageById(Integer messageId) {
        return ticketMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket message not found"));
    }

    @Transactional
    public void archiveMessageByTicketId(Integer ticketId) {
        ticketMessageRepository.findByTicketId(ticketId).forEach(ticketMessage -> {
            ticketMessage.setArchive(true);
            ticketMessageRepository.save(ticketMessage);
        });
    }

    private TicketMessageDTO convertToDTO(TicketMessage message) {
        TicketMessageDTO messageDTO = new TicketMessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setMessageText(message.getMessageText());
        messageDTO.setInsertDate(message.getInsertDate().toString());
        messageDTO.setTicketId(message.getTicket().getId());
        messageDTO.setSenderId(message.getSender().getId());
        return messageDTO;
    }
}
