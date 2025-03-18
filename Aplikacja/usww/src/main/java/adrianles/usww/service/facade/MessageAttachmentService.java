package adrianles.usww.service.facade;

import adrianles.usww.api.dto.MessageAttachmentDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Transactional(readOnly = true)
public interface MessageAttachmentService {

    List<MessageAttachmentDTO> getAllAttachmentsByMessageId(Integer messageId);

    MessageAttachmentDTO getAttachmentById(Integer attachmentId);

    @Transactional
    MessageAttachmentDTO createAttachment(Integer messageId, MultipartFile file) throws IOException;

    @Transactional
    void deleteAttachment(Integer attachmentId);

}
