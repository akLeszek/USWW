package adrianles.usww.domain.entity;

import adrianles.usww.domain.entity.dictionary.TicketCategory;
import adrianles.usww.domain.entity.dictionary.TicketPriority;
import adrianles.usww.domain.entity.dictionary.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TICKET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "operator_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_ticket_operator"))
    private User operator;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_ticket_student"))
    private User student;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_ticket_status"))
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_ticket_category"))
    private TicketCategory category;

    @ManyToOne
    @JoinColumn(name = "priority_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_ticket_priority"))
    private TicketPriority priority;

    @NotNull(message = "Ticket title cannot be null")
    @Size(max = 30, message = "Ticket title cannot exceed 30 characters")
    @Column(nullable = false, length = 30)
    private String title;

    private LocalDateTime insertedDate;
    private LocalDateTime changeDate;
    private LocalDateTime lastActivityDate;

    @Column(nullable = false)
    private boolean archive = false;
}
