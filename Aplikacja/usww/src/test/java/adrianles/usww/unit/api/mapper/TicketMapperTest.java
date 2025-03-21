package adrianles.usww.unit.api.mapper;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.mapper.TicketMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.entity.dictionary.TicketCategory;
import adrianles.usww.domain.entity.dictionary.TicketPriority;
import adrianles.usww.domain.entity.dictionary.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TicketMapperTest {

    private TicketMapper ticketMapper;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticketMapper = new TicketMapper();

        User operator = new User();
        operator.setId(1);

        User student = new User();
        student.setId(2);

        TicketStatus status = new TicketStatus();
        status.setId(1);

        TicketCategory category = new TicketCategory();
        category.setId(1);

        TicketPriority priority = new TicketPriority();
        priority.setId(1);

        ticket = new Ticket();
        ticket.setId(1);
        ticket.setTitle("Test Ticket");
        ticket.setInsertedDate(LocalDateTime.now());
        ticket.setChangeDate(LocalDateTime.now());
        ticket.setOperator(operator);
        ticket.setStudent(student);
        ticket.setStatus(status);
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setArchive(false);
    }

    @Test
    @DisplayName("Mapowanie Ticket do TicketDTO powinno poprawnie przypisywać wartości")
    void toDto_shouldMapTicketToDtoCorrectly() {
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        assertThat(ticketDTO).isNotNull();
        assertThat(ticketDTO.getId()).isEqualTo(ticket.getId());
        assertThat(ticketDTO.getTitle()).isEqualTo(ticket.getTitle());
        assertThat(ticketDTO.getInsertedDate()).isEqualTo(ticket.getInsertedDate().toString());
        assertThat(ticketDTO.getChangeDate()).isEqualTo(ticket.getChangeDate().toString());
        assertThat(ticketDTO.getOperatorId()).isEqualTo(ticket.getOperator().getId());
        assertThat(ticketDTO.getStudentId()).isEqualTo(ticket.getStudent().getId());
        assertThat(ticketDTO.getStatusId()).isEqualTo(ticket.getStatus().getId());
        assertThat(ticketDTO.getCategoryId()).isEqualTo(ticket.getCategory().getId());
        assertThat(ticketDTO.getPriorityId()).isEqualTo(ticket.getPriority().getId());
        assertThat(ticketDTO.isArchive()).isEqualTo(ticket.isArchive());
    }

    @Test
    @DisplayName("Mapowanie Ticket do TicketDTO z wartościami null powinna obsłużyć poprawnie")
    void toDto_shouldHandleNullValues() {
        Ticket nullTicket = new Ticket();
        nullTicket.setId(2);

        TicketDTO ticketDTO = ticketMapper.toDto(nullTicket);

        assertThat(ticketDTO).isNotNull();
        assertThat(ticketDTO.getId()).isEqualTo(2);
        assertThat(ticketDTO.getInsertedDate()).isNull();
        assertThat(ticketDTO.getChangeDate()).isNull();
        assertThat(ticketDTO.getOperatorId()).isNull();
        assertThat(ticketDTO.getStudentId()).isNull();
        assertThat(ticketDTO.getStatusId()).isNull();
        assertThat(ticketDTO.getCategoryId()).isNull();
        assertThat(ticketDTO.getPriorityId()).isNull();
    }
}
