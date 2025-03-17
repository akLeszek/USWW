package adrianles.usww.service.impl;

import adrianles.usww.api.dto.TicketMessageDTO;
import adrianles.usww.api.mapper.TicketMessageMapper;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.repository.TicketMessageRepository;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.TicketMessageService;
import adrianles.usww.service.facade.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketMessageServiceImpl implements TicketMessageService {
    private final TicketMessageRepository ticketMessageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;
    private final TicketMessageMapper ticketMessageMapper;

    @Override
    public List<TicketMessageDTO> getAllMessagesByTicketId(Integer ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new IllegalArgumentException("Ticket id " + ticketId + " does not exist");
        }

        return ticketMessageRepository.findByTicketId(ticketId).stream()
                .map(ticketMessageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
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
        return ticketMessageMapper.toDto(savedMessage);
    }

    @Override
    public TicketMessageDTO archiveMessage(Integer messageId) {
        TicketMessage ticketMessage = findTicketMessageById(messageId);
        ticketMessage.setArchive(true);
        TicketMessage archivedMessage = ticketMessageRepository.save(ticketMessage);
        return ticketMessageMapper.toDto(archivedMessage);
    }

    private TicketMessage findTicketMessageById(Integer messageId) {
        return ticketMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket message not found"));
    }

    @Override
    public void archiveMessageByTicketId(Integer ticketId) {
        ticketMessageRepository.findByTicketId(ticketId).forEach(ticketMessage -> {
            ticketMessage.setArchive(true);
            ticketMessageRepository.save(ticketMessage);
        });
    }
}
