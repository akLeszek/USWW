package adrianles.usww.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "TICKET_MESSAGE")
public class TicketMessage extends AbstractEntity {

    private Ticket ticket;
    private User sender;
    private String message;
    private Timestamp insertDate;
    private boolean archive;

    public TicketMessage() {
    }

    public TicketMessage(Ticket ticket, User sender, String message, Timestamp insertDate, boolean archive) {
        this.ticket = ticket;
        this.sender = sender;
        this.message = message;
        this.insertDate = insertDate;
        this.archive = archive;
    }

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @ManyToOne
    @JoinColumn(name = "sender_id")
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Column(name = "message_text")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "insert_date")
    public Timestamp getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Timestamp insertDate) {
        this.insertDate = insertDate;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }
}
