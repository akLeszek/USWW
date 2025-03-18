package adrianles.usww.api.mapper;

import adrianles.usww.api.dto.MessageAttachmentDTO;
import adrianles.usww.domain.entity.MessageAttachment;
import org.springframework.stereotype.Component;

@Component
public class MessageAttachmentMapper {

    public MessageAttachmentDTO toDto(MessageAttachment attachment) {
        if (attachment == null) {
            return null;
        }

        MessageAttachmentDTO attachmentDTO = new MessageAttachmentDTO();
        attachmentDTO.setId(attachment.getId());
        attachmentDTO.setMessageId(attachment.getMessage().getId());
        attachmentDTO.setAttachment(attachment.getAttachment());
        attachmentDTO.setFileName(attachment.getFileName());
        return attachmentDTO;
    }
}
