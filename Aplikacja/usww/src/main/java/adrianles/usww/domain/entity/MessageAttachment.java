package adrianles.usww.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "MESSAGE_ATTACHMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachment extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "message_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_message_attachment_ticket_message"))
    private TicketMessage message;

    @Column(length = 255)
    private String fileName;

    @Lob
    private byte[] attachment;
}
