package adrianles.usww.service.impl;

import adrianles.usww.api.dto.MessageAttachmentDTO;
import adrianles.usww.api.mapper.MessageAttachmentMapper;
import adrianles.usww.domain.entity.MessageAttachment;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.repositiory.MessageAttachmentRepository;
import adrianles.usww.domain.repositiory.TicketMessageRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.MessageAttachmentService;
import adrianles.usww.service.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageAttachmentServiceImpl implements MessageAttachmentService {

    private final MessageAttachmentRepository messageAttachmentRepository;
    private final TicketMessageRepository ticketMessageRepository;
    private final MessageAttachmentMapper messageAttachmentMapper;

    @Override
    public List<MessageAttachmentDTO> getAllAttachmentsByMessageId(Integer messageId) {
        return messageAttachmentRepository.findByMessageId(messageId).stream()
                .map(messageAttachmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageAttachmentDTO getAttachmentById(Integer attachmentId) {
        MessageAttachment attachment = findAttachmentById(attachmentId);
        return messageAttachmentMapper.toDto(attachment);
    }

    @Override
    public MessageAttachmentDTO createAttachment(Integer messageId, MultipartFile file) throws IOException {
        FileValidator.validateFile(file);

        TicketMessage message = ticketMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        MessageAttachment attachment = new MessageAttachment();
        attachment.setMessage(message);
        attachment.setAttachment(file.getBytes());
        attachment.setFileName(file.getOriginalFilename());

        MessageAttachment savedAttachment = messageAttachmentRepository.save(attachment);
        return messageAttachmentMapper.toDto(savedAttachment);
    }

    @Override
    public void deleteAttachment(Integer attachmentId) {
        MessageAttachment attachment = findAttachmentById(attachmentId);
        messageAttachmentRepository.delete(attachment);
    }

    private MessageAttachment findAttachmentById(Integer attachmentId) {
        return messageAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));
    }
}
