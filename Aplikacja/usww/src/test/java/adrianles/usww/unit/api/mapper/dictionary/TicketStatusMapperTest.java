package adrianles.usww.unit.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.TicketStatusDTO;
import adrianles.usww.api.mapper.dictionary.TicketStatusMapper;
import adrianles.usww.domain.entity.dictionary.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketStatusMapperTest {

    private TicketStatusMapper ticketStatusMapper;
    private TicketStatus ticketStatus;

    @BeforeEach
    void setUp() {
        ticketStatusMapper = new TicketStatusMapper();
        ticketStatus = new TicketStatus();
        ticketStatus.setId(1);
        ticketStatus.setIdn("NEW");
        ticketStatus.setName("Nowy");
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję TicketStatus na DTO")
    void toDto_shouldMapEntityToDto() {
        // when
        TicketStatusDTO result = ticketStatusMapper.toDto(ticketStatus);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdn()).isEqualTo("NEW");
        assertThat(result.getName()).isEqualTo("Nowy");
    }

    @Test
    @DisplayName("Powinien zwrócić null gdy encja jest null")
    void toDto_shouldReturnNullWhenEntityIsNull() {
        // when
        TicketStatusDTO result = ticketStatusMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję z polami null")
    void toDto_shouldMapEntityWithNullFields() {
        // given
        TicketStatus statusWithNulls = new TicketStatus();
        statusWithNulls.setId(2);
        statusWithNulls.setIdn(null);
        statusWithNulls.setName(null);

        // when
        TicketStatusDTO result = ticketStatusMapper.toDto(statusWithNulls);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getIdn()).isNull();
        assertThat(result.getName()).isNull();
    }

    @Test
    @DisplayName("Powinien poprawnie implementować metodę createDto")
    void createDto_shouldCreateProperDtoInstance() {
        // when
        TicketStatusDTO result = ticketStatusMapper.toDto(ticketStatus);

        // then
        assertThat(result).isInstanceOf(TicketStatusDTO.class);
    }
}
