package adrianles.usww.domain.entity;

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

public class TicketMessageTest {

    private Validator validator;
    private TicketMessage ticketMessage;
    private Ticket ticket;
    private User sender;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        ticket = new Ticket();
        ticket.setId(1);
        ticket.setTitle("Problem z logowaniem");

        sender = new User();
        sender.setId(1);
        sender.setLogin("user");

        ticketMessage = new TicketMessage();
        ticketMessage.setId(1);
        ticketMessage.setTicket(ticket);
        ticketMessage.setSender(sender);
        ticketMessage.setMessageText("Treść wiadomości testowej");
        ticketMessage.setInsertDate(LocalDateTime.now());
        ticketMessage.setArchive(false);
    }

    @Test
    @DisplayName("Powinien poprawnie stworzyć obiekt TicketMessage")
    void shouldCreateTicketMessage() {
        assertThat(ticketMessage).isNotNull();
        assertThat(ticketMessage.getId()).isEqualTo(1);
        assertThat(ticketMessage.getTicket()).isEqualTo(ticket);
        assertThat(ticketMessage.getSender()).isEqualTo(sender);
        assertThat(ticketMessage.getMessageText()).isEqualTo("Treść wiadomości testowej");
        assertThat(ticketMessage.getInsertDate()).isNotNull();
        assertThat(ticketMessage.isArchive()).isFalse();
    }

    @Test
    @DisplayName("Powinien zwrócić błąd walidacji dla braku wymaganego pola ticket")
    void shouldFailValidationWhenTicketIsNull() {
        ticketMessage.setTicket(null);

        Set<ConstraintViolation<TicketMessage>> violations = validator.validate(ticketMessage);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Ticket cannot be null");
    }

    @Test
    @DisplayName("Powinien zwrócić błąd walidacji dla braku wymaganego pola sender")
    void shouldFailValidationWhenSenderIsNull() {
        ticketMessage.setSender(null);

        Set<ConstraintViolation<TicketMessage>> violations = validator.validate(ticketMessage);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Sender cannot be null");
    }

    @Test
    @DisplayName("Powinien poprawnie przejść walidację dla wszystkich wymaganych pól")
    void shouldPassValidationWithAllRequiredFields() {
        Set<ConstraintViolation<TicketMessage>> violations = validator.validate(ticketMessage);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Powinien poprawnie wykorzystać konstruktory")
    void shouldUseConstructors() {
        TicketMessage emptyMessage = new TicketMessage();

        TicketMessage fullMessage = new TicketMessage(
                ticket,
                sender,
                "Nowa wiadomość testowa",
                LocalDateTime.now(),
                false
        );

        fullMessage.setId(2);

        assertThat(emptyMessage).isNotNull();

        assertThat(fullMessage).isNotNull();
        assertThat(fullMessage.getId()).isEqualTo(2);
        assertThat(fullMessage.getTicket()).isEqualTo(ticket);
        assertThat(fullMessage.getSender()).isEqualTo(sender);
        assertThat(fullMessage.getMessageText()).isEqualTo("Nowa wiadomość testowa");
        assertThat(fullMessage.getInsertDate()).isNotNull();
        assertThat(fullMessage.isArchive()).isFalse();
    }

    @Test
    @DisplayName("Powinien poprawnie porównać dwa identyczne obiekty")
    void shouldCompareEqualObjects() {
        TicketMessage message2 = new TicketMessage();
        message2.setId(1);
        message2.setTicket(ticket);
        message2.setSender(sender);
        message2.setMessageText("Treść wiadomości testowej");
        message2.setInsertDate(ticketMessage.getInsertDate());
        message2.setArchive(false);

        assertThat(ticketMessage).isEqualTo(ticketMessage);
        assertThat(ticketMessage.hashCode()).isEqualTo(ticketMessage.hashCode());

        assertThat(ticketMessage).isEqualTo(message2);
        assertThat(ticketMessage.hashCode()).isEqualTo(message2.hashCode());
    }

    @Test
    @DisplayName("Powinien poprawnie rozróżnić dwa różne obiekty")
    void shouldDifferentiateNonEqualObjects() {
        TicketMessage message2 = new TicketMessage();
        message2.setId(2);
        message2.setTicket(ticket);
        message2.setSender(sender);
        message2.setMessageText("Treść wiadomości testowej");
        message2.setInsertDate(ticketMessage.getInsertDate());
        message2.setArchive(false);

        assertThat(ticketMessage).isNotEqualTo(null);

        assertThat(ticketMessage).isNotEqualTo(new Object());

        assertThat(ticketMessage).isNotEqualTo(message2);
        assertThat(ticketMessage.hashCode()).isNotEqualTo(message2.hashCode());
    }
}
