package adrianles.usww.unit.service.impl;

import adrianles.usww.api.dto.TicketMessageDTO;
import adrianles.usww.api.mapper.TicketMessageMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.TicketMessage;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.repository.TicketMessageRepository;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.facade.TicketService;
import adrianles.usww.service.impl.TicketMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketMessageServiceImplTest {

    @Mock
    private TicketMessageRepository ticketMessageRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketService ticketService;

    @Mock
    private TicketMessageMapper ticketMessageMapper;

    @InjectMocks
    private TicketMessageServiceImpl ticketMessageService;

    private Ticket ticket;
    private User sender;
    private TicketMessage ticketMessage1;
    private TicketMessage ticketMessage2;
    private TicketMessageDTO ticketMessageDTO1;
    private TicketMessageDTO ticketMessageDTO2;
    private TicketMessageDTO newMessageDTO;

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych
        ticket = new Ticket();
        ticket.setId(1);
        ticket.setTitle("Test Ticket");

        sender = new User();
        sender.setId(1);
        sender.setLogin("test_user");

        ticketMessage1 = new TicketMessage();
        ticketMessage1.setId(1);
        ticketMessage1.setTicket(ticket);
        ticketMessage1.setSender(sender);
        ticketMessage1.setMessageText("Test message 1");
        ticketMessage1.setInsertDate(LocalDateTime.now());
        ticketMessage1.setArchive(false);

        ticketMessage2 = new TicketMessage();
        ticketMessage2.setId(2);
        ticketMessage2.setTicket(ticket);
        ticketMessage2.setSender(sender);
        ticketMessage2.setMessageText("Test message 2");
        ticketMessage2.setInsertDate(LocalDateTime.now().plusHours(1));
        ticketMessage2.setArchive(false);

        ticketMessageDTO1 = new TicketMessageDTO();
        ticketMessageDTO1.setId(1);
        ticketMessageDTO1.setTicketId(1);
        ticketMessageDTO1.setSenderId(1);
        ticketMessageDTO1.setMessageText("Test message 1");
        ticketMessageDTO1.setInsertDate(ticketMessage1.getInsertDate().toString());

        ticketMessageDTO2 = new TicketMessageDTO();
        ticketMessageDTO2.setId(2);
        ticketMessageDTO2.setTicketId(1);
        ticketMessageDTO2.setSenderId(1);
        ticketMessageDTO2.setMessageText("Test message 2");
        ticketMessageDTO2.setInsertDate(ticketMessage2.getInsertDate().toString());

        newMessageDTO = new TicketMessageDTO();
        newMessageDTO.setTicketId(1);
        newMessageDTO.setSenderId(1);
        newMessageDTO.setMessageText("New test message");
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie wiadomości dla danego ID zgłoszenia")
    void shouldGetAllMessagesByTicketId() {
        // given
        when(ticketRepository.existsById(1)).thenReturn(true);
        when(ticketMessageRepository.findByTicketId(1)).thenReturn(Arrays.asList(ticketMessage1, ticketMessage2));
        when(ticketMessageMapper.toDto(ticketMessage1)).thenReturn(ticketMessageDTO1);
        when(ticketMessageMapper.toDto(ticketMessage2)).thenReturn(ticketMessageDTO2);

        // when
        List<TicketMessageDTO> result = ticketMessageService.getAllMessagesByTicketId(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(ticketMessageDTO1, ticketMessageDTO2);

        verify(ticketRepository).existsById(1);
        verify(ticketMessageRepository).findByTicketId(1);
        verify(ticketMessageMapper).toDto(ticketMessage1);
        verify(ticketMessageMapper).toDto(ticketMessage2);
    }

    @Test
    @DisplayName("Powinien zgłosić błąd, gdy ID zgłoszenia nie istnieje")
    void shouldThrowExceptionWhenTicketIdDoesNotExist() {
        // given
        when(ticketRepository.existsById(999)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> ticketMessageService.getAllMessagesByTicketId(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ticket id 999 does not exist");

        verify(ticketRepository).existsById(999);
        verifyNoInteractions(ticketMessageRepository);
        verifyNoInteractions(ticketMessageMapper);
    }

    @Test
    @DisplayName("Powinien utworzyć nową wiadomość")
    void shouldCreateMessage() {
        // given
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(ticketMessageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> {
            TicketMessage savedMessage = invocation.getArgument(0);
            savedMessage.setId(3);
            return savedMessage;
        });
        when(ticketMessageMapper.toDto(any(TicketMessage.class))).thenReturn(ticketMessageDTO1);
        doNothing().when(ticketService).updateLastActivityDate(anyInt());

        // when
        TicketMessageDTO result = ticketMessageService.createMessage(newMessageDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(ticketMessageDTO1);

        verify(ticketRepository).findById(1);
        verify(userRepository).findById(1);
        verify(ticketMessageRepository).save(any(TicketMessage.class));
        verify(ticketService).updateLastActivityDate(1);
        verify(ticketMessageMapper).toDto(any(TicketMessage.class));
    }

    @Test
    @DisplayName("Powinien zgłosić błąd, gdy nie znaleziono zgłoszenia przy tworzeniu wiadomości")
    void shouldThrowExceptionWhenTicketNotFoundDuringMessageCreation() {
        // given
        TicketMessageDTO invalidTicketDTO = new TicketMessageDTO();
        invalidTicketDTO.setTicketId(999); // Używamy innego ID niż w newMessageDTO
        invalidTicketDTO.setSenderId(1);
        invalidTicketDTO.setMessageText("Invalid ticket test");

        when(ticketRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ticketMessageService.createMessage(invalidTicketDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Invalid ticketId");

        verify(ticketRepository).findById(999);
        verifyNoInteractions(ticketMessageMapper);
        verifyNoMoreInteractions(ticketRepository);
        verifyNoInteractions(ticketMessageRepository);
    }

    @Test
    @DisplayName("Powinien zgłosić błąd, gdy nie znaleziono użytkownika przy tworzeniu wiadomości")
    void shouldThrowExceptionWhenUserNotFoundDuringMessageCreation() {
        // given
        TicketMessageDTO invalidSenderDTO = new TicketMessageDTO();
        invalidSenderDTO.setTicketId(1);
        invalidSenderDTO.setSenderId(999); // Używamy innego ID niż w newMessageDTO
        invalidSenderDTO.setMessageText("Invalid sender test");

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ticketMessageService.createMessage(invalidSenderDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Invalid senderId");

        verify(ticketRepository).findById(1);
        verify(userRepository).findById(999);
        verifyNoInteractions(ticketMessageMapper);
        verifyNoInteractions(ticketMessageRepository);
    }

    @Test
    @DisplayName("Powinien zarchiwizować wiadomość")
    void shouldArchiveMessage() {
        // given
        when(ticketMessageRepository.findById(1)).thenReturn(Optional.of(ticketMessage1));
        when(ticketMessageRepository.save(ticketMessage1)).thenReturn(ticketMessage1);
        when(ticketMessageMapper.toDto(ticketMessage1)).thenReturn(ticketMessageDTO1);

        // when
        TicketMessageDTO result = ticketMessageService.archiveMessage(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(ticketMessageDTO1);
        assertThat(ticketMessage1.isArchive()).isTrue();

        verify(ticketMessageRepository).findById(1);
        verify(ticketMessageRepository).save(ticketMessage1);
        verify(ticketMessageMapper).toDto(ticketMessage1);
    }

    @Test
    @DisplayName("Powinien zgłosić błąd, gdy nie znaleziono wiadomości do zarchiwizowania")
    void shouldThrowExceptionWhenMessageNotFoundDuringArchiving() {
        // given
        when(ticketMessageRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ticketMessageService.archiveMessage(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ticket message not found");

        verify(ticketMessageRepository).findById(999);
        verifyNoMoreInteractions(ticketMessageRepository);
        verifyNoInteractions(ticketMessageMapper);
    }

    @Test
    @DisplayName("Powinien zarchiwizować wszystkie wiadomości dla danego zgłoszenia")
    void shouldArchiveMessagesByTicketId() {
        // given
        List<TicketMessage> messages = Arrays.asList(ticketMessage1, ticketMessage2);
        when(ticketMessageRepository.findByTicketId(1)).thenReturn(messages);
        when(ticketMessageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ticketMessageService.archiveMessageByTicketId(1);

        // then
        assertThat(ticketMessage1.isArchive()).isTrue();
        assertThat(ticketMessage2.isArchive()).isTrue();

        verify(ticketMessageRepository).findByTicketId(1);
        verify(ticketMessageRepository, times(2)).save(any(TicketMessage.class));
    }
}
