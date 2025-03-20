package adrianles.usww.unit.domain.entity;

import adrianles.usww.domain.entity.MessageAttachment;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.entity.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageAttachmentTest {

    private Validator validator;
    private MessageAttachment messageAttachment;
    private TicketMessage message;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Ticket ticket = new Ticket();
        ticket.setId(1);

        User sender = new User();
        sender.setId(1);

        message = new TicketMessage();
        message.setId(1);
        message.setTicket(ticket);
        message.setSender(sender);

        messageAttachment = new MessageAttachment();
        messageAttachment.setId(1);
        messageAttachment.setMessage(message);
        messageAttachment.setFilename("test_document.pdf");
        messageAttachment.setAttachment("Sample attachment content".getBytes());
    }

    @Test
    @DisplayName("Powinien poprawnie stworzyć obiekt MessageAttachment")
    void shouldCreateMessageAttachment() {
        assertThat(messageAttachment).isNotNull();
        assertThat(messageAttachment.getId()).isEqualTo(1);
        assertThat(messageAttachment.getMessage()).isEqualTo(message);
        assertThat(messageAttachment.getFilename()).isEqualTo("test_document.pdf");
        assertThat(messageAttachment.getAttachment()).isNotNull();
        assertThat(new String(messageAttachment.getAttachment())).isEqualTo("Sample attachment content");
    }

    @Test
    @DisplayName("Powinien poprawnie przejść walidację dla wszystkich wymaganych pól")
    void shouldPassValidationWithAllRequiredFields() {
        var violations = validator.validate(messageAttachment);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("Powinien poprawnie obsługiwać różne formaty plików")
    @ValueSource(strings = {"document.pdf", "image.jpg", "picture.png"})
    void shouldHandleDifferentFileFormats(String filename) {
        messageAttachment.setFilename(filename);
        assertThat(messageAttachment.getFilename()).isEqualTo(filename);

        var violations = validator.validate(messageAttachment);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Powinien obsługiwać duże pliki binarne")
    void shouldHandleLargeBinaryContent() {
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        Arrays.fill(largeContent, (byte) 'A');

        messageAttachment.setAttachment(largeContent);

        assertThat(messageAttachment.getAttachment()).isEqualTo(largeContent);
        assertThat(messageAttachment.getAttachment().length).isEqualTo(1024 * 1024);
    }

    @Test
    @DisplayName("Powinien poprawnie wykorzystać konstruktory")
    void shouldUseConstructors() {
        MessageAttachment emptyAttachment = new MessageAttachment();

        byte[] attachmentData = "New attachment data".getBytes();
        MessageAttachment fullAttachment = new MessageAttachment(
                message,
                "testAttachment.pdf",
                attachmentData
        );

        fullAttachment.setId(2);

        assertThat(emptyAttachment).isNotNull();

        assertThat(fullAttachment).isNotNull();
        assertThat(fullAttachment.getId()).isEqualTo(2);
        assertThat(fullAttachment.getMessage()).isEqualTo(message);
        assertThat(fullAttachment.getFilename()).isEqualTo("testAttachment.pdf");
        assertThat(fullAttachment.getAttachment()).isEqualTo(attachmentData);
    }

    @Test
    @DisplayName("Powinien poprawnie porównać dwa identyczne obiekty")
    void shouldCompareEqualObjects() {
        MessageAttachment attachment2 = new MessageAttachment();
        attachment2.setId(1);
        attachment2.setMessage(message);
        attachment2.setFilename("test_document.pdf");
        attachment2.setAttachment("Sample attachment content".getBytes());

        assertThat(messageAttachment).isEqualTo(messageAttachment);
        assertThat(messageAttachment.hashCode()).isEqualTo(messageAttachment.hashCode());

        assertThat(messageAttachment).isEqualTo(attachment2);
        assertThat(messageAttachment.hashCode()).isEqualTo(attachment2.hashCode());
    }

    @Test
    @DisplayName("Powinien poprawnie rozróżnić dwa różne obiekty")
    void shouldDifferentiateNonEqualObjects() {
        MessageAttachment attachment2 = new MessageAttachment();
        attachment2.setId(2);
        attachment2.setMessage(message);
        attachment2.setFilename("different_document.pdf");
        attachment2.setAttachment("Sample attachment content".getBytes());

        assertThat(messageAttachment).isNotEqualTo(null);
        assertThat(messageAttachment).isNotEqualTo(new Object());
        assertThat(messageAttachment).isNotEqualTo(attachment2);
        assertThat(messageAttachment.hashCode()).isNotEqualTo(attachment2.hashCode());
    }
}
