package adrianles.usww.unit.api.controller;

import adrianles.usww.api.controller.common.TicketController;
import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import adrianles.usww.service.facade.TicketArchiveService;
import adrianles.usww.service.facade.TicketPriorityService;
import adrianles.usww.service.facade.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private TicketArchiveService ticketArchiveService;

    @Mock
    private TicketPriorityService ticketPriorityService;

    @InjectMocks
    private TicketController ticketController;

    private TicketDTO ticket1;
    private TicketDTO ticket2;
    private List<TicketDTO> tickets;

    @BeforeEach
    void setUp() {
        ticket1 = new TicketDTO();
        ticket1.setId(1);
        ticket1.setTitle("Problem z logowaniem");
        ticket1.setStatusId(1);
        ticket1.setCategoryId(1);
        ticket1.setPriorityId(1);
        ticket1.setStudentId(1);
        ticket1.setOperatorId(2);
        ticket1.setInsertedDate(LocalDateTime.now().toString());
        ticket1.setArchive(false);

        ticket2 = new TicketDTO();
        ticket2.setId(2);
        ticket2.setTitle("Brak dostępu do kursu");
        ticket2.setStatusId(2);
        ticket2.setCategoryId(2);
        ticket2.setPriorityId(2);
        ticket2.setStudentId(1);
        ticket2.setOperatorId(2);
        ticket2.setInsertedDate(LocalDateTime.now().toString());
        ticket2.setArchive(false);

        tickets = Arrays.asList(ticket1, ticket2);
    }

    @Test
    @DisplayName("getAllTickets powinien zwrócić listę wszystkich zgłoszeń")
    void getAllTickets_shouldReturnAllTickets() {
        // given
        when(ticketService.getAllTickets()).thenReturn(tickets);

        // when
        ResponseEntity<List<TicketDTO>> response = ticketController.getAllTickets();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly(ticket1, ticket2);
        verify(ticketService).getAllTickets();
    }

    @Test
    @DisplayName("getActiveTickets powinien zwrócić listę aktywnych zgłoszeń")
    void getActiveTickets_shouldReturnActiveTickets() {
        // given
        when(ticketArchiveService.getActiveTickets()).thenReturn(tickets);

        // when
        ResponseEntity<List<TicketDTO>> response = ticketController.getActiveTickets();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly(ticket1, ticket2);
        verify(ticketArchiveService).getActiveTickets();
    }

    @Test
    @DisplayName("getArchiveTickets powinien zwrócić listę zarchiwizowanych zgłoszeń")
    void getArchiveTickets_shouldReturnArchivedTickets() {
        // given
        when(ticketArchiveService.getArchivedTickets()).thenReturn(tickets);

        // when
        ResponseEntity<List<TicketDTO>> response = ticketController.getArchiveTickets();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly(ticket1, ticket2);
        verify(ticketArchiveService).getArchivedTickets();
    }

    @Test
    @DisplayName("getTicketById powinien zwrócić zgłoszenie o podanym id")
    void getTicketById_shouldReturnTicketWithGivenId() {
        // given
        when(ticketService.getTicketById(1)).thenReturn(ticket1);

        // when
        ResponseEntity<TicketDTO> response = ticketController.getTicketById(1);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ticket1);
        verify(ticketService).getTicketById(1);
    }

    @Test
    @DisplayName("createTicket powinien utworzyć nowe zgłoszenie")
    void createTicket_shouldCreateNewTicket() {
        // given
        when(ticketService.createTicket(any(TicketDTO.class))).thenReturn(ticket1);

        // when
        ResponseEntity<TicketDTO> response = ticketController.createTicket(ticket1);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ticket1);
        verify(ticketService).createTicket(ticket1);
    }

    @Test
    @DisplayName("updateTicket powinien zaktualizować istniejące zgłoszenie")
    void updateTicket_shouldUpdateExistingTicket() {
        // given
        when(ticketService.updateTicket(eq(1), any(TicketDTO.class))).thenReturn(ticket1);

        // when
        ResponseEntity<TicketDTO> response = ticketController.updateTicket(1, ticket1);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ticket1);
        verify(ticketService).updateTicket(1, ticket1);
    }

    @Test
    @DisplayName("updateTicketPriority powinien zaktualizować priorytet zgłoszenia")
    void updateTicketPriority_shouldUpdateTicketPriority() {
        // given
        when(ticketPriorityService.updateTicketPriority(1, 2)).thenReturn(ticket1);

        // when
        ResponseEntity<TicketDTO> response = ticketController.updateTicketPriority(1, 2);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ticket1);
        verify(ticketPriorityService).updateTicketPriority(1, 2);
    }

    @Test
    @DisplayName("archiveTicket powinien zarchiwizować zgłoszenie")
    void archiveTicket_shouldArchiveTicket() {
        // given
        when(ticketArchiveService.archiveTicket(1)).thenReturn(ticket1);

        // when
        ResponseEntity<TicketDTO> response = ticketController.archiveTicket(1);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ticket1);
        verify(ticketArchiveService).archiveTicket(1);
    }

    @Test
    @DisplayName("restoreTicket powinien przywrócić zgłoszenie")
    void restoreTicket_shouldRestoreTicket() {
        // given
        when(ticketArchiveService.restoreTicket(1)).thenReturn(ticket1);

        // when
        ResponseEntity<TicketDTO> response = ticketController.restoreTicket(1);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ticket1);
        verify(ticketArchiveService).restoreTicket(1);
    }

    @Test
    @DisplayName("searchTickets powinien zwrócić przefiltrowane zgłoszenia")
    void searchTickets_shouldReturnFilteredTickets() {
        // given
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        criteria.setTitle("problem");
        criteria.setCategoryId(1);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "id", "asc");
        Page<TicketDTO> ticketPage = new PageImpl<>(List.of(ticket1));

        when(ticketService.getFilteredTickets(any(TicketFilterCriteriaDTO.class), any(PageRequest.class)))
                .thenReturn(ticketPage);

        // when
        ResponseEntity<Page<TicketDTO>> response = ticketController.searchTickets(
                criteria, 0, 10, "id", "asc");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ticketPage);
        assertThat(Objects.requireNonNull(response.getBody()).getContent()).containsExactly(ticket1);
        verify(ticketService).getFilteredTickets(any(), any(PageRequest.class));
    }
}
