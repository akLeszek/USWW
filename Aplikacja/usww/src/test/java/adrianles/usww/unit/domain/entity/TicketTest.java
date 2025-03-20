package adrianles.usww.domain.entity;

import adrianles.usww.domain.entity.dictionary.TicketCategory;
import adrianles.usww.domain.entity.dictionary.TicketPriority;
import adrianles.usww.domain.entity.dictionary.TicketStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TicketTest {

    private Validator validator;
    private Ticket ticket;
    private User operator;
    private User student;
    private TicketStatus status;
    private TicketCategory category;
    private TicketPriority priority;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        operator = new User();
        operator.setId(1);
        operator.setLogin("operator");

        student = new User();
        student.setId(2);
        student.setLogin("student");

        status = new TicketStatus();
        status.setId(1);
        status.setIdn("NOWE");
        status.setName("Nowe");

        category = new TicketCategory();
        category.setId(1);
        category.setIdn("LOGOWANIE");
        category.setName("Problemy z logowaniem");

        priority = new TicketPriority();
        priority.setId(1);
        priority.setIdn("MEDIUM");
        priority.setName("Średni");

        ticket = new Ticket();
        ticket.setId(1);
        ticket.setOperator(operator);
        ticket.setStudent(student);
        ticket.setStatus(status);
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setTitle("Problem z logowaniem");
        ticket.setInsertedDate(LocalDateTime.now());
        ticket.setChangeDate(LocalDateTime.now());
        ticket.setLastActivityDate(LocalDateTime.now());
        ticket.setArchive(false);
    }

    @Test
    @DisplayName("Powinien poprawnie stworzyć obiekt Ticket")
    void shouldCreateTicket() {
        assertThat(ticket).isNotNull();
        assertThat(ticket.getId()).isEqualTo(1);
        assertThat(ticket.getOperator()).isEqualTo(operator);
        assertThat(ticket.getStudent()).isEqualTo(student);
        assertThat(ticket.getStatus()).isEqualTo(status);
        assertThat(ticket.getCategory()).isEqualTo(category);
        assertThat(ticket.getPriority()).isEqualTo(priority);
        assertThat(ticket.getTitle()).isEqualTo("Problem z logowaniem");
        assertThat(ticket.getInsertedDate()).isNotNull();
        assertThat(ticket.getChangeDate()).isNotNull();
        assertThat(ticket.getLastActivityDate()).isNotNull();
        assertThat(ticket.isArchive()).isFalse();
    }

    @Test
    @DisplayName("Powinien zwrócić błąd walidacji dla braku wymaganego pola title")
    void shouldFailValidationWhenTitleIsNull() {
        ticket.setTitle(null);

        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);

        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Ticket title cannot be null");
    }

    @Test
    @DisplayName("Powinien poprawnie przejść walidację dla wszystkich wymaganych pól")
    void shouldPassValidationWithAllRequiredFields() {
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Powinien poprawnie wykorzystać konstruktory")
    void shouldUseConstructors() {
        Ticket emptyTicket = new Ticket();

        Ticket fullTicket = new Ticket(
                operator,
                student,
                status,
                category,
                priority,
                "Nowy problem",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false
        );

        fullTicket.setId(2);

        assertThat(emptyTicket).isNotNull();

        assertThat(fullTicket).isNotNull();
        assertThat(fullTicket.getId()).isEqualTo(2);
        assertThat(fullTicket.getOperator()).isEqualTo(operator);
        assertThat(fullTicket.getStudent()).isEqualTo(student);
        assertThat(fullTicket.getStatus()).isEqualTo(status);
        assertThat(fullTicket.getCategory()).isEqualTo(category);
        assertThat(fullTicket.getPriority()).isEqualTo(priority);
        assertThat(fullTicket.getTitle()).isEqualTo("Nowy problem");
        assertThat(fullTicket.getInsertedDate()).isNotNull();
        assertThat(fullTicket.getChangeDate()).isNotNull();
        assertThat(fullTicket.getLastActivityDate()).isNotNull();
        assertThat(fullTicket.isArchive()).isFalse();
    }

    @Test
    @DisplayName("Powinien poprawnie porównać dwa identyczne obiekty")
    void shouldCompareEqualObjects() {
        Ticket ticket2 = new Ticket();
        ticket2.setId(1);
        ticket2.setOperator(operator);
        ticket2.setStudent(student);
        ticket2.setStatus(status);
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setTitle("Problem z logowaniem");
        ticket2.setInsertedDate(ticket.getInsertedDate());
        ticket2.setChangeDate(ticket.getChangeDate());
        ticket2.setLastActivityDate(ticket.getLastActivityDate());
        ticket2.setArchive(false);

        assertThat(ticket).isEqualTo(ticket);
        assertThat(ticket.hashCode()).isEqualTo(ticket.hashCode());

        assertThat(ticket).isEqualTo(ticket2);
        assertThat(ticket.hashCode()).isEqualTo(ticket2.hashCode());
    }

    @Test
    @DisplayName("Powinien poprawnie rozróżnić dwa różne obiekty")
    void shouldDifferentiateNonEqualObjects() {
        Ticket ticket2 = new Ticket();
        ticket2.setId(2);
        ticket2.setOperator(operator);
        ticket2.setStudent(student);
        ticket2.setStatus(status);
        ticket2.setCategory(category);
        ticket2.setPriority(priority);
        ticket2.setTitle("Problem z logowaniem");
        ticket2.setInsertedDate(ticket.getInsertedDate());
        ticket2.setChangeDate(ticket.getChangeDate());
        ticket2.setLastActivityDate(ticket.getLastActivityDate());
        ticket2.setArchive(false);

        assertThat(ticket).isNotEqualTo(null);

        assertThat(ticket).isNotEqualTo(new Object());

        assertThat(ticket).isNotEqualTo(ticket2);
        assertThat(ticket.hashCode()).isNotEqualTo(ticket2.hashCode());
    }
}
