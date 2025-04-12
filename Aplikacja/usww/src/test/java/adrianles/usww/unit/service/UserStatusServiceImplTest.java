package adrianles.usww.unit.service;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.api.mapper.UserMapper;
import adrianles.usww.domain.entity.Ticket;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.repository.TicketRepository;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.impl.UserStatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserStatusServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserStatusServiceImpl userStatusService;

    private User testUser;
    private UserDTO testUserDTO;
    private List<Ticket> testTickets;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setLogin("testuser");
        testUser.setLoginBan(false);
        testUser.setArchive(false);

        testUserDTO = new UserDTO();
        testUserDTO.setId(1);
        testUserDTO.setLogin("testuser");
        testUserDTO.setLoginBan(false);
        testUserDTO.setArchive(false);

        Ticket ticket1 = new Ticket();
        ticket1.setId(1);
        ticket1.setTitle("Test Ticket 1");
        ticket1.setArchive(false);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2);
        ticket2.setTitle("Test Ticket 2");
        ticket2.setArchive(false);

        testTickets = Arrays.asList(ticket1, ticket2);
    }

    @Test
    @DisplayName("blockUser powinien zablokować użytkownika i zwrócić zaktualizowane DTO")
    void blockUserShouldBlockUserAndReturnUpdatedDTO() {
        // given
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDTO);

        // when
        UserDTO result = userStatusService.blockUser(1);

        // then
        assertThat(testUser.isLoginBan()).isTrue();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());

        verify(userRepository).findById(1);
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("blockUser powinien rzucić wyjątek gdy użytkownik nie istnieje")
    void blockUserShouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> userStatusService.blockUser(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");

        verify(userRepository).findById(999);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("unblockUser powinien odblokować użytkownika i zwrócić zaktualizowane DTO")
    void unblockUserShouldUnblockUserAndReturnUpdatedDTO() {
        // given
        testUser.setLoginBan(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDTO);

        // when
        UserDTO result = userStatusService.unblockUser(1);

        // then
        assertThat(testUser.isLoginBan()).isFalse();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());

        verify(userRepository).findById(1);
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("unblockUser powinien rzucić wyjątek gdy użytkownik nie istnieje")
    void unblockUserShouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> userStatusService.unblockUser(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");

        verify(userRepository).findById(999);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("archiveUser powinien zarchiwizować użytkownika i jego zgłoszenia")
    void archiveUserShouldArchiveUserAndTickets() {
        // given
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDTO);
        when(ticketRepository.findByStudentId(anyInt())).thenReturn(testTickets);
        when(ticketRepository.findByOperatorId(anyInt())).thenReturn(Collections.emptyList());

        // when
        UserDTO result = userStatusService.archiveUser(1);

        // then
        assertThat(testUser.isArchive()).isTrue();
        assertThat(testUser.isLoginBan()).isTrue();
        assertThat(result).isNotNull();

        // Sprawdzamy, czy wszystkie zgłoszenia zostały zarchiwizowane
        for (Ticket ticket : testTickets) {
            assertThat(ticket.isArchive()).isTrue();
        }

        verify(userRepository).findById(1);
        verify(userRepository).save(testUser);
        verify(ticketRepository).findByStudentId(1);
        verify(ticketRepository).findByOperatorId(1);
        verify(ticketRepository, times(testTickets.size())).save(any(Ticket.class));
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("archiveUser powinien rzucić wyjątek gdy użytkownik nie istnieje")
    void archiveUserShouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> userStatusService.archiveUser(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");

        verify(userRepository).findById(999);
        verifyNoMoreInteractions(userRepository, ticketRepository, userMapper);
    }

    @Test
    @DisplayName("getArchivedUsers powinien zwrócić listę zarchiwizowanych użytkowników")
    void getArchivedUsersShouldReturnListOfArchivedUsers() {
        // given
        List<User> archivedUsers = Collections.singletonList(testUser);
        List<UserDTO> archivedUserDTOs = Collections.singletonList(testUserDTO);

        when(userRepository.findAllByArchiveTrue()).thenReturn(archivedUsers);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDTO);

        // when
        List<UserDTO> result = userStatusService.getArchivedUsers();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testUserDTO.getId());

        verify(userRepository).findAllByArchiveTrue();
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("restoreUser powinien przywrócić użytkownika ze stanu zarchiwizowanego")
    void restoreUserShouldUnarchiveUser() {
        // given
        testUser.setArchive(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDTO);

        // when
        UserDTO result = userStatusService.restoreUser(1);

        // then
        assertThat(testUser.isArchive()).isFalse();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());

        verify(userRepository).findById(1);
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("restoreUser powinien rzucić wyjątek gdy użytkownik nie istnieje")
    void restoreUserShouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> userStatusService.restoreUser(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");

        verify(userRepository).findById(999);
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}
