package adrianles.usww.unit.api.mapper;

import adrianles.usww.api.dto.TicketMessageDTO;
import adrianles.usww.api.mapper.TicketMessageMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TicketMessageMapperTest {

    private TicketMessageMapper ticketMessageMapper;
    private TicketMessage ticketMessage;

    @BeforeEach
    void setUp() {
        ticketMessageMapper = new TicketMessageMapper();

        Ticket ticket = new Ticket();
        ticket.setId(1);

        User sender = new User();
        sender.setId(2);

        ticketMessage = new TicketMessage();
        ticketMessage.setId(3);
        ticketMessage.setTicket(ticket);
        ticketMessage.setSender(sender);
        ticketMessage.setMessageText("Test message");
        ticketMessage.setInsertDate(LocalDateTime.of(2024, 2, 16, 10, 0));
    }

    @Test
    @DisplayName("Mapper powinien poprawnie mapować TicketMessage na TicketMessageDTO")
    void shouldMapTicketMessageToDTO() {
        // When
        TicketMessageDTO result = ticketMessageMapper.toDto(ticketMessage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getTicketId()).isEqualTo(1);
        assertThat(result.getSenderId()).isEqualTo(2);
        assertThat(result.getMessageText()).isEqualTo("Test message");
        assertThat(result.getInsertDate()).isEqualTo("2024-02-16T10:00");
    }

    @Test
    @DisplayName("Mapper powinien obsłużyć null TicketMessage")
    void shouldHandleNullTicketMessage() {
        // When
        TicketMessageDTO result = ticketMessageMapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Mapper powinien obsłużyć TicketMessage bez niektórych pól")
    void shouldHandleTicketMessageWithMissingFields() {
        // Given
        TicketMessage incompleteMessage = new TicketMessage();
        incompleteMessage.setId(4);

        // When
        TicketMessageDTO result = ticketMessageMapper.toDto(incompleteMessage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getTicketId()).isNull();
        assertThat(result.getSenderId()).isNull();
        assertThat(result.getMessageText()).isNull();
        assertThat(result.getInsertDate()).isNull();
    }
}
