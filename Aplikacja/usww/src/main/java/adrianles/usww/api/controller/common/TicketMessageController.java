package adrianles.usww.api.controller.common;

import adrianles.usww.api.dto.MessageAttachmentDTO;
import adrianles.usww.api.dto.TicketMessageDTO;
import adrianles.usww.service.facade.MessageAttachmentService;
import adrianles.usww.service.facade.TicketMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class TicketMessageController {
    private final TicketMessageService ticketMessageService;
    private final MessageAttachmentService messageAttachmentService;

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketMessageDTO>> getTicketMessage(@PathVariable("ticketId") int ticketId) {
        return ResponseEntity.ok(ticketMessageService.getAllMessagesByTicketId(ticketId));
    }

    @PostMapping
    public ResponseEntity<TicketMessageDTO> addTicketMessage(@Valid @RequestBody TicketMessageDTO ticketMessageDTO) {
        return ResponseEntity.ok(ticketMessageService.createMessage(ticketMessageDTO));
    }

    @GetMapping("/{messageId}/attachments")
    public ResponseEntity<List<MessageAttachmentDTO>> getMessageAttachments(@PathVariable("messageId") int messageId) {
        return ResponseEntity.ok(messageAttachmentService.getAllAttachmentsByMessageId(messageId));
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<byte[]> getAttachment(@PathVariable("attachmentId") int attachmentId) {
        MessageAttachmentDTO attachment = messageAttachmentService.getAttachmentById(attachmentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        String filename = attachment.getFilename() != null ?
                attachment.getFilename() : "attachment-" + attachmentId;
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(attachment.getAttachment(), headers, HttpStatus.OK);
    }

    @PostMapping("/{messageId}/attachments")
    public ResponseEntity<MessageAttachmentDTO> addAttachment(
            @PathVariable("messageId") int messageId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(messageAttachmentService.createAttachment(messageId, file));
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable("attachmentId") int attachmentId) {
        messageAttachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
