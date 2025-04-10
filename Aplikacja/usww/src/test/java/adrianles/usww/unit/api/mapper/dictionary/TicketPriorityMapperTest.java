package adrianles.usww.unit.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.TicketPriorityDTO;
import adrianles.usww.api.mapper.dictionary.TicketPriorityMapper;
import adrianles.usww.domain.entity.dictionary.TicketPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketPriorityMapperTest {

    private TicketPriorityMapper ticketPriorityMapper;
    private TicketPriority ticketPriority;

    @BeforeEach
    void setUp() {
        ticketPriorityMapper = new TicketPriorityMapper();
        ticketPriority = new TicketPriority();
        ticketPriority.setId(1);
        ticketPriority.setIdn("HIGH");
        ticketPriority.setName("Wysoki");
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję TicketPriority na DTO")
    void toDto_shouldMapEntityToDto() {
        // when
        TicketPriorityDTO result = ticketPriorityMapper.toDto(ticketPriority);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdn()).isEqualTo("HIGH");
        assertThat(result.getName()).isEqualTo("Wysoki");
    }

    @Test
    @DisplayName("Powinien zwrócić null gdy encja jest null")
    void toDto_shouldReturnNullWhenEntityIsNull() {
        // when
        TicketPriorityDTO result = ticketPriorityMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję z polami null")
    void toDto_shouldMapEntityWithNullFields() {
        // given
        TicketPriority priorityWithNulls = new TicketPriority();
        priorityWithNulls.setId(2);
        priorityWithNulls.setIdn(null);
        priorityWithNulls.setName(null);

        // when
        TicketPriorityDTO result = ticketPriorityMapper.toDto(priorityWithNulls);

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
        TicketPriorityDTO result = ticketPriorityMapper.toDto(ticketPriority);

        // then
        assertThat(result).isInstanceOf(TicketPriorityDTO.class);
    }
}
