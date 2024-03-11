package adrianles.usww.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "MESSAGE_ATTACHMENT")
public class MessageAttachment extends AbstractEntity {
    private TicketMessage message;

    private byte[] attachment;

    public MessageAttachment() {
    }

    public MessageAttachment(TicketMessage message, byte[] attachment) {
        this.message = message;
        this.attachment = attachment;
    }

    @ManyToOne
    @JoinColumn(name = "message_id")
    public TicketMessage getMessage() {
        return message;
    }

    public void setMessage(TicketMessage message) {
        this.message = message;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
}
