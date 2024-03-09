package adrianles.usww.entity;

import adrianles.usww.entity.dictionary.TicketCategory;
import adrianles.usww.entity.dictionary.TicketStatus;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "TICKET")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TicketCategory category;

    @Column(name = "inserted_date")
    private Timestamp insertedDate;

    @Column(name = "change_date")
    private Timestamp changeDate;
    private boolean archive;

    public Ticket() {
    }

    public Ticket(Integer id, User operator, User student, TicketStatus status, TicketCategory category,
                  Timestamp insertedDate, Timestamp changeDate, boolean archive) {
        this.id = id;
        this.operator = operator;
        this.student = student;
        this.status = status;
        this.category = category;
        this.insertedDate = insertedDate;
        this.changeDate = changeDate;
        this.archive = archive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public Timestamp getInsertedDate() {
        return insertedDate;
    }

    public void setInsertedDate(Timestamp insertedDate) {
        this.insertedDate = insertedDate;
    }

    public Timestamp getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Timestamp changeDate) {
        this.changeDate = changeDate;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }
}
