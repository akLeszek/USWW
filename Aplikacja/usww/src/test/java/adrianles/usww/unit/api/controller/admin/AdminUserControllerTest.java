package adrianles.usww.unit.api.controller.admin;

import adrianles.usww.api.controller.admin.AdminUserController;
import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.service.facade.UserService;
import adrianles.usww.service.facade.UserStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserStatusService userStatusService;

    @InjectMocks
    private AdminUserController adminUserController;

    private UserDTO testUser1;
    private UserDTO testUser2;
    private List<UserDTO> userList;

    @BeforeEach
    void setUp() {
        testUser1 = new UserDTO();
        testUser1.setId(1);
        testUser1.setLogin("user1");
        testUser1.setForename("Jan");
        testUser1.setSurname("Kowalski");
        testUser1.setGroupId(1);

        testUser2 = new UserDTO();
        testUser2.setId(2);
        testUser2.setLogin("user2");
        testUser2.setForename("Anna");
        testUser2.setSurname("Nowak");
        testUser2.setGroupId(2);

        userList = Arrays.asList(testUser1, testUser2);
    }

    @Test
    @DisplayName("Powinien zwrócić listę wszystkich użytkowników")
    void getAllUsers_shouldReturnAllUsers() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDTO>> response = adminUserController.getAllUsers();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(testUser1, testUser2);
        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Powinien zwrócić użytkownika o podanym identyfikatorze")
    void getUserById_shouldReturnUserWithGivenId() {
        // Arrange
        when(userService.getUserById(1)).thenReturn(testUser1);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.getUserById(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(testUser1);
        verify(userService).getUserById(1);
    }

    @Test
    @DisplayName("Powinien utworzyć nowego użytkownika")
    void createUser_shouldCreateNewUser() {
        // Arrange
        UserDTO newUser = new UserDTO();
        newUser.setLogin("newuser");
        newUser.setGroupId(1);

        when(userService.createUser(any(UserDTO.class))).thenReturn(testUser1);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.createUser(newUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(testUser1);
        verify(userService).createUser(newUser);
    }

    @Test
    @DisplayName("Powinien zablokować użytkownika")
    void blockUser_shouldBlockUser() {
        // Arrange
        UserDTO blockedUser = new UserDTO();
        blockedUser.setId(1);
        blockedUser.setLogin("user1");
        blockedUser.setLoginBan(true);

        when(userStatusService.blockUser(1)).thenReturn(blockedUser);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.blockUser(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isLoginBan()).isTrue();
        verify(userStatusService).blockUser(1);
    }

    @Test
    @DisplayName("Powinien odblokować użytkownika")
    void unblockUser_shouldUnblockUser() {
        // Arrange
        UserDTO unblockedUser = new UserDTO();
        unblockedUser.setId(1);
        unblockedUser.setLogin("user1");
        unblockedUser.setLoginBan(false);

        when(userStatusService.unblockUser(1)).thenReturn(unblockedUser);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.unblockUser(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isLoginBan()).isFalse();
        verify(userStatusService).unblockUser(1);
    }

    @Test
    @DisplayName("Powinien zarchiwizować użytkownika")
    void archiveUser_shouldArchiveUser() {
        // Arrange
        UserDTO archivedUser = new UserDTO();
        archivedUser.setId(1);
        archivedUser.setLogin("user1");
        archivedUser.setArchive(true);

        when(userStatusService.archiveUser(1)).thenReturn(archivedUser);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.archiveUser(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isArchive()).isTrue();
        verify(userStatusService).archiveUser(1);
    }

    @Test
    @DisplayName("Powinien zwrócić listę zarchiwizowanych użytkowników")
    void getArchivedUsers_shouldReturnArchivedUsers() {
        // Arrange
        UserDTO archivedUser1 = new UserDTO();
        archivedUser1.setId(3);
        archivedUser1.setLogin("archived1");
        archivedUser1.setArchive(true);

        UserDTO archivedUser2 = new UserDTO();
        archivedUser2.setId(4);
        archivedUser2.setLogin("archived2");
        archivedUser2.setArchive(true);

        List<UserDTO> archivedUsers = Arrays.asList(archivedUser1, archivedUser2);

        when(userStatusService.getArchivedUsers()).thenReturn(archivedUsers);

        // Act
        ResponseEntity<List<UserDTO>> response = adminUserController.getArchivedUsers();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).contains(archivedUser1, archivedUser2);
        verify(userStatusService).getArchivedUsers();
    }

    @Test
    @DisplayName("Powinien przywrócić zarchiwizowanego użytkownika")
    void restoreUser_shouldRestoreArchivedUser() {
        // Arrange
        UserDTO restoredUser = new UserDTO();
        restoredUser.setId(1);
        restoredUser.setLogin("user1");
        restoredUser.setArchive(false);

        when(userStatusService.restoreUser(1)).thenReturn(restoredUser);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.restoreUser(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isArchive()).isFalse();
        verify(userStatusService).restoreUser(1);
    }
}
