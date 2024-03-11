package adrianles.usww.entity;

import adrianles.usww.entity.dictionary.TicketCategory;
import adrianles.usww.entity.dictionary.TicketStatus;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "TICKET")
public class Ticket extends AbstractEntity {

    private User operator;
    private User student;
    private TicketStatus status;
    private TicketCategory category;
    private Timestamp insertedDate;
    private Timestamp changeDate;
    private boolean archive;

    public Ticket() {
    }

    public Ticket(User operator, User student, TicketStatus status, TicketCategory category,
                  Timestamp insertedDate, Timestamp changeDate, boolean archive) {
        this.operator = operator;
        this.student = student;
        this.status = status;
        this.category = category;
        this.insertedDate = insertedDate;
        this.changeDate = changeDate;
        this.archive = archive;
    }


    @ManyToOne
    @JoinColumn(name = "operator_id")
    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    @ManyToOne
    @JoinColumn(name = "student_id")
    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    @ManyToOne
    @JoinColumn(name = "status_id")
    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    @Column(name = "inserted_date")
    public Timestamp getInsertedDate() {
        return insertedDate;
    }

    public void setInsertedDate(Timestamp insertedDate) {
        this.insertedDate = insertedDate;
    }

    @Column(name = "change_date")
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
