package adrianles.usww.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "TICKET_MESSAGE")
public class TicketMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "message_text")
    private String message;

    @Column(name = "insert_date")
    private Timestamp insertDate;

    private boolean archive;

    public TicketMessage() {
    }

    public TicketMessage(Integer id, Ticket ticket, User sender, String message, Timestamp insertDate, boolean archive) {
        this.id = id;
        this.ticket = ticket;
        this.sender = sender;
        this.message = message;
        this.insertDate = insertDate;
        this.archive = archive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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
