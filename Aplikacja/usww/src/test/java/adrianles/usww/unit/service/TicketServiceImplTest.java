package adrianles.usww.unit.service;

import adrianles.usww.api.dto.TicketDTO;
import adrianles.usww.api.dto.TicketFilterCriteriaDTO;
import adrianles.usww.api.mapper.TicketMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.entity.dictionary.TicketCategory;
import adrianles.usww.domain.entity.dictionary.TicketPriority;
import adrianles.usww.domain.entity.dictionary.TicketStatus;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.domain.repository.dictionary.TicketCategoryRepository;
import adrianles.usww.domain.repository.dictionary.TicketPriorityRepository;
import adrianles.usww.domain.repository.dictionary.TicketStatusRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketCategoryRepository ticketCategoryRepository;

    @Mock
    private TicketStatusRepository ticketStatusRepository;

    @Mock
    private TicketPriorityRepository ticketPriorityRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket ticket;
    private TicketDTO ticketDTO;
    private User operator;
    private User student;
    private TicketCategory category;
    private TicketStatus status;
    private TicketPriority priority;

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych
        operator = new User();
        operator.setId(1);
        operator.setLogin("operator");

        student = new User();
        student.setId(2);
        student.setLogin("student");

        category = new TicketCategory();
        category.setId(1);
        category.setIdn("LOGOWANIE");
        category.setName("Problemy z logowaniem");

        status = new TicketStatus();
        status.setId(1);
        status.setIdn("NEW");
        status.setName("Nowe");

        priority = new TicketPriority();
        priority.setId(1);
        priority.setIdn("MEDIUM");
        priority.setName("Średni");

        ticket = new Ticket();
        ticket.setId(1);
        ticket.setTitle("Problem z logowaniem");
        ticket.setOperator(operator);
        ticket.setStudent(student);
        ticket.setCategory(category);
        ticket.setStatus(status);
        ticket.setPriority(priority);
        ticket.setInsertedDate(LocalDateTime.now());
        ticket.setChangeDate(LocalDateTime.now());
        ticket.setLastActivityDate(LocalDateTime.now());
        ticket.setArchive(false);

        ticketDTO = new TicketDTO();
        ticketDTO.setId(1);
        ticketDTO.setTitle("Problem z logowaniem");
        ticketDTO.setOperatorId(1);
        ticketDTO.setStudentId(2);
        ticketDTO.setCategoryId(1);
        ticketDTO.setStatusId(1);
        ticketDTO.setPriorityId(1);
        ticketDTO.setArchive(false);
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie zgłoszenia")
    void shouldGetAllTickets() {
        // given
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        when(ticketRepository.findAll()).thenReturn(tickets);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDTO);

        // when
        List<TicketDTO> result = ticketService.getAllTickets();

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(ticketDTO);

        verify(ticketRepository).findAll();
        verify(ticketMapper).toDto(any(Ticket.class));
    }

    @Test
    @DisplayName("Powinien zwrócić filtrowane zgłoszenia")
    void shouldGetFilteredTickets() {
        // given
        List<Ticket> tickets = List.of(ticket);
        Page<Ticket> ticketPage = new PageImpl<>(tickets);
        TicketFilterCriteriaDTO criteria = new TicketFilterCriteriaDTO();
        Pageable pageable = Pageable.unpaged();

        when(ticketRepository.findAll(Mockito.<Specification<Ticket>>any(), eq(pageable))).thenReturn(ticketPage);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDTO);

        // when
        Page<TicketDTO> result = ticketService.getFilteredTickets(criteria, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(ticketDTO);

        verify(ticketRepository).findAll(Mockito.<Specification<Ticket>>any(), eq(pageable));
        verify(ticketMapper).toDto(any(Ticket.class));
    }

    @Test
    @DisplayName("Powinien zwrócić zgłoszenie po ID")
    void shouldGetTicketById() {
        // given
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(ticketMapper.toDto(ticket)).thenReturn(ticketDTO);

        // when
        TicketDTO result = ticketService.getTicketById(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(ticketDTO);

        verify(ticketRepository).findById(1);
        verify(ticketMapper).toDto(ticket);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek gdy nie znaleziono zgłoszenia")
    void shouldThrowExceptionWhenTicketNotFound() {
        // given
        when(ticketRepository.findById(99)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketById(99));

        verify(ticketRepository).findById(99);
        verify(ticketMapper, never()).toDto(any(Ticket.class));
    }

    @Test
    @DisplayName("Powinien utworzyć nowe zgłoszenie")
    void shouldCreateTicket() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.of(operator));
        when(userRepository.findById(2)).thenReturn(Optional.of(student));
        when(ticketCategoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(ticketStatusRepository.findById(1)).thenReturn(Optional.of(status));
        when(ticketPriorityRepository.findById(1)).thenReturn(Optional.of(priority));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDTO);

        // when
        TicketDTO result = ticketService.createTicket(ticketDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(ticketDTO);

        verify(ticketRepository).save(any(Ticket.class));
        verify(ticketMapper).toDto(any(Ticket.class));
    }

    @Test
    @DisplayName("Powinien zaktualizować zgłoszenie")
    void shouldUpdateTicket() {
        // given
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(1)).thenReturn(Optional.of(operator));
        when(userRepository.findById(2)).thenReturn(Optional.of(student));
        when(ticketCategoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(ticketStatusRepository.findById(1)).thenReturn(Optional.of(status));
        when(ticketPriorityRepository.findById(1)).thenReturn(Optional.of(priority));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDTO);

        TicketDTO updateDTO = new TicketDTO();
        updateDTO.setId(1);
        updateDTO.setTitle("Zaktualizowany problem");
        updateDTO.setOperatorId(1);
        updateDTO.setStudentId(2);
        updateDTO.setCategoryId(1);
        updateDTO.setStatusId(1);
        updateDTO.setPriorityId(1);

        // when
        TicketDTO result = ticketService.updateTicket(1, updateDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(ticketDTO);

        verify(ticketRepository).findById(1);
        verify(ticketRepository).save(any(Ticket.class));
        verify(ticketMapper).toDto(any(Ticket.class));
    }

    @Test
    @DisplayName("Powinien zaktualizować datę ostatniej aktywności")
    void shouldUpdateLastActivityDate() {
        // given
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // when
        ticketService.updateLastActivityDate(1);

        // then
        verify(ticketRepository).findById(1);
        verify(ticketRepository).save(ticket);
    }
}
