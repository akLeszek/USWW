package adrianles.usww.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MESSAGE_ATTACHMENT")
public class MessageAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private TicketMessage message;

    private byte[] attachment;

    public MessageAttachment() {
    }

    public MessageAttachment(Integer id, TicketMessage message, byte[] attachment) {
        this.id = id;
        this.message = message;
        this.attachment = attachment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
