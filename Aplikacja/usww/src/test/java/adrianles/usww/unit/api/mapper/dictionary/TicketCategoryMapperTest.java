package adrianles.usww.unit.api.mapper.dictionary;

import adrianles.usww.api.dto.dictionary.TicketCategoryDTO;
import adrianles.usww.api.mapper.dictionary.TicketCategoryMapper;
import adrianles.usww.domain.entity.dictionary.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketCategoryMapperTest {

    private TicketCategoryMapper ticketCategoryMapper;
    private TicketCategory ticketCategory;

    @BeforeEach
    void setUp() {
        ticketCategoryMapper = new TicketCategoryMapper();
        ticketCategory = new TicketCategory();
        ticketCategory.setId(1);
        ticketCategory.setIdn("LOGOWANIE");
        ticketCategory.setName("Problemy z logowaniem");
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję TicketCategory na DTO")
    void toDto_shouldMapEntityToDto() {
        // when
        TicketCategoryDTO result = ticketCategoryMapper.toDto(ticketCategory);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdn()).isEqualTo("LOGOWANIE");
        assertThat(result.getName()).isEqualTo("Problemy z logowaniem");
    }

    @Test
    @DisplayName("Powinien zwrócić null gdy encja jest null")
    void toDto_shouldReturnNullWhenEntityIsNull() {
        // when
        TicketCategoryDTO result = ticketCategoryMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Powinien poprawnie mapować encję z polami null")
    void toDto_shouldMapEntityWithNullFields() {
        // given
        TicketCategory categoryWithNulls = new TicketCategory();
        categoryWithNulls.setId(2);
        categoryWithNulls.setIdn(null);
        categoryWithNulls.setName(null);

        // when
        TicketCategoryDTO result = ticketCategoryMapper.toDto(categoryWithNulls);

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
        TicketCategoryDTO result = ticketCategoryMapper.toDto(ticketCategory);

        // then
        assertThat(result).isInstanceOf(TicketCategoryDTO.class);
    }
}
