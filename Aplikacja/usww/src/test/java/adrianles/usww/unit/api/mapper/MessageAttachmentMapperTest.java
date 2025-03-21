package adrianles.usww.unit.api.mapper;

import adrianles.usww.api.dto.MessageAttachmentDTO;
import adrianles.usww.api.mapper.MessageAttachmentMapper;
import adrianles.usww.domain.entity.MessageAttachment;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.TicketMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageAttachmentMapperTest {

    private MessageAttachmentMapper messageAttachmentMapper;
    private MessageAttachment messageAttachment;
    private TicketMessage ticketMessage;

    @BeforeEach
    void setUp() {
        messageAttachmentMapper = new MessageAttachmentMapper();

        Ticket ticket = new Ticket();
        ticket.setId(1);

        ticketMessage = new TicketMessage();
        ticketMessage.setId(1);
        ticketMessage.setTicket(ticket);

        messageAttachment = new MessageAttachment();
        messageAttachment.setId(1);
        messageAttachment.setMessage(ticketMessage);
        messageAttachment.setFilename("test_document.pdf");
        messageAttachment.setAttachment("Sample attachment content".getBytes());
    }

    @Test
    @DisplayName("Powinna poprawnie mapować MessageAttachment na MessageAttachmentDTO")
    void shouldMapMessageAttachmentToDTO() {
        MessageAttachmentDTO attachmentDTO = messageAttachmentMapper.toDto(messageAttachment);

        assertThat(attachmentDTO).isNotNull();
        assertThat(attachmentDTO.getId()).isEqualTo(1);
        assertThat(attachmentDTO.getMessageId()).isEqualTo(1);
        assertThat(attachmentDTO.getFilename()).isEqualTo("test_document.pdf");
        assertThat(new String(attachmentDTO.getAttachment())).isEqualTo("Sample attachment content");
    }

    @Test
    @DisplayName("Powinna obsłużyć null podczas mapowania")
    void shouldHandleNullWhenMapping() {
        MessageAttachmentDTO attachmentDTO = messageAttachmentMapper.toDto(null);

        assertThat(attachmentDTO).isNull();
    }
}
