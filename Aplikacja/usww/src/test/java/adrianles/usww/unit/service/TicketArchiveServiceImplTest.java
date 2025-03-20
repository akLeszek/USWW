package adrianles.usww.unit.service;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.mapper.TicketMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.repository.TicketMessageRepository;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.impl.TicketArchiveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketArchiveServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMessageRepository ticketMessageRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketArchiveServiceImpl ticketArchiveService;

    private Ticket activeTicket;
    private Ticket archivedTicket;
    private TicketDTO activeTicketDTO;
    private TicketDTO archivedTicketDTO;
    private TicketMessage message1;
    private TicketMessage message2;

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych
        activeTicket = new Ticket();
        activeTicket.setId(1);
        activeTicket.setTitle("Active Ticket");
        activeTicket.setArchive(false);
        activeTicket.setInsertedDate(LocalDateTime.now());
        activeTicket.setChangeDate(LocalDateTime.now());

        archivedTicket = new Ticket();
        archivedTicket.setId(2);
        archivedTicket.setTitle("Archived Ticket");
        archivedTicket.setArchive(true);
        archivedTicket.setInsertedDate(LocalDateTime.now());
        archivedTicket.setChangeDate(LocalDateTime.now());

        activeTicketDTO = new TicketDTO();
        activeTicketDTO.setId(1);
        activeTicketDTO.setTitle("Active Ticket");
        activeTicketDTO.setArchive(false);

        archivedTicketDTO = new TicketDTO();
        archivedTicketDTO.setId(2);
        archivedTicketDTO.setTitle("Archived Ticket");
        archivedTicketDTO.setArchive(true);

        message1 = new TicketMessage();
        message1.setId(1);
        message1.setTicket(activeTicket);
        message1.setArchive(false);

        message2 = new TicketMessage();
        message2.setId(2);
        message2.setTicket(activeTicket);
        message2.setArchive(false);
    }

    @Test
    @DisplayName("Powinien zwrócić listę aktywnych zgłoszeń")
    void shouldReturnActiveTickets() {
        // given
        when(ticketRepository.findByArchiveFalse()).thenReturn(Arrays.asList(activeTicket));
        when(ticketMapper.toDto(activeTicket)).thenReturn(activeTicketDTO);

        // when
        List<TicketDTO> result = ticketArchiveService.getActiveTickets();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(activeTicketDTO);
        verify(ticketRepository).findByArchiveFalse();
        verify(ticketMapper).toDto(activeTicket);
    }

    @Test
    @DisplayName("Powinien zwrócić pustą listę gdy brak aktywnych zgłoszeń")
    void shouldReturnEmptyListWhenNoActiveTickets() {
        // given
        when(ticketRepository.findByArchiveFalse()).thenReturn(Collections.emptyList());

        // when
        List<TicketDTO> result = ticketArchiveService.getActiveTickets();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(ticketRepository).findByArchiveFalse();
        verify(ticketMapper, never()).toDto(any(Ticket.class));
    }

    @Test
    @DisplayName("Powinien zwrócić listę zarchiwizowanych zgłoszeń")
    void shouldReturnArchivedTickets() {
        // given
        when(ticketRepository.findByArchiveTrue()).thenReturn(Arrays.asList(archivedTicket));
        when(ticketMapper.toDto(archivedTicket)).thenReturn(archivedTicketDTO);

        // when
        List<TicketDTO> result = ticketArchiveService.getArchivedTickets();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(archivedTicketDTO);
        verify(ticketRepository).findByArchiveTrue();
        verify(ticketMapper).toDto(archivedTicket);
    }

    @Test
    @DisplayName("Powinien zarchiwizować zgłoszenie wraz z wiadomościami")
    void shouldArchiveTicketWithMessages() {
        // given
        when(ticketRepository.findById(1)).thenReturn(Optional.of(activeTicket));
        when(ticketMessageRepository.findByTicketId(1)).thenReturn(Arrays.asList(message1, message2));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(activeTicket);
        when(ticketMapper.toDto(activeTicket)).thenReturn(activeTicketDTO);

        // when
        TicketDTO result = ticketArchiveService.archiveTicket(1);

        // then
        assertThat(result).isNotNull();
        assertThat(activeTicket.isArchive()).isTrue();
        assertThat(activeTicket.getChangeDate()).isNotNull();

        verify(ticketRepository).findById(1);
        verify(ticketMessageRepository).findByTicketId(1);
        verify(ticketRepository).save(activeTicket);
        verify(ticketMessageRepository, times(2)).save(any(TicketMessage.class));

        assertThat(message1.isArchive()).isTrue();
        assertThat(message2.isArchive()).isTrue();
    }

    @Test
    @DisplayName("Powinien przywrócić zgłoszenie wraz z wiadomościami")
    void shouldRestoreTicketWithMessages() {
        // given
        when(ticketRepository.findById(2)).thenReturn(Optional.of(archivedTicket));
        when(ticketMessageRepository.findByTicketId(2)).thenReturn(Arrays.asList(message1, message2));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(archivedTicket);
        when(ticketMapper.toDto(archivedTicket)).thenReturn(archivedTicketDTO);

        // when
        TicketDTO result = ticketArchiveService.restoreTicket(2);

        // then
        assertThat(result).isNotNull();
        assertThat(archivedTicket.isArchive()).isFalse();
        assertThat(archivedTicket.getChangeDate()).isNotNull();

        verify(ticketRepository).findById(2);
        verify(ticketMessageRepository).findByTicketId(2);
        verify(ticketRepository).save(archivedTicket);
        verify(ticketMessageRepository, times(2)).save(any(TicketMessage.class));

        assertThat(message1.isArchive()).isFalse();
        assertThat(message2.isArchive()).isFalse();
    }

    @Test
    @DisplayName("Powinien zgłosić wyjątek gdy zgłoszenie nie istnieje przy archiwizacji")
    void shouldThrowExceptionWhenTicketNotFoundDuringArchive() {
        // given
        when(ticketRepository.findById(999)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> ticketArchiveService.archiveTicket(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ticket not found");

        verify(ticketRepository).findById(999);
        verify(ticketMessageRepository, never()).findByTicketId(anyInt());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Powinien zgłosić wyjątek gdy zgłoszenie nie istnieje przy przywracaniu")
    void shouldThrowExceptionWhenTicketNotFoundDuringRestore() {
        // given
        when(ticketRepository.findById(999)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> ticketArchiveService.restoreTicket(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ticket not found");

        verify(ticketRepository).findById(999);
        verify(ticketMessageRepository, never()).findByTicketId(anyInt());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
