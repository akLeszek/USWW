package adrianles.usww.unit.api.controller;

import adrianles.usww.api.controller.common.UserController;
import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.service.facade.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setLogin("testuser");
        userDTO.setForename("Jan");
        userDTO.setSurname("Kowalski");
        userDTO.setGroupId(1);
        userDTO.setOrganizationUnitId(1);

        authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("getCurrentUserProfile powinien zwrócić profil aktualnie zalogowanego użytkownika")
    void getCurrentUserProfile_shouldReturnCurrentUserProfile() {
        // given
        when(userService.getUserByLogin("testuser")).thenReturn(userDTO);

        // when
        ResponseEntity<UserDTO> response = userController.getCurrentUserProfile();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userDTO);
        verify(userService).getUserByLogin("testuser");
    }

    @Test
    @DisplayName("getUserBasicInfo powinien zwrócić podstawowe informacje o użytkowniku")
    void getUserBasicInfo_shouldReturnUserBasicInfo() {
        // given
        when(userService.getUserBasicInfo(1)).thenReturn(userDTO);

        // when
        ResponseEntity<UserDTO> response = userController.getUserBasicInfo(1);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userDTO);
        verify(userService).getUserBasicInfo(1);
    }

    @Test
    @DisplayName("getUserBasicInfo powinien przekazać poprawne ID do serwisu")
    void getUserBasicInfo_shouldPassCorrectIdToService() {
        // given
        when(userService.getUserBasicInfo(42)).thenReturn(userDTO);

        // when
        ResponseEntity<UserDTO> response = userController.getUserBasicInfo(42);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).getUserBasicInfo(42);
    }

    @Test
    @DisplayName("getCurrentUserProfile powinien obsłużyć pustą autentykację")
    void getCurrentUserProfile_shouldHandleEmptyAuthentication() {
        // given
        SecurityContextHolder.clearContext(); // Usuń kontekst autentykacji

        // Symuluj pustą, ale istniejącą autentykację
        Authentication emptyAuth = new UsernamePasswordAuthenticationToken(null, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(emptyAuth);

        // when
        ResponseEntity<UserDTO> response = userController.getCurrentUserProfile();

        // then
        // Oczekujemy, że metoda zostanie wywołana z pustym stringiem, a nie z null
        verify(userService).getUserByLogin("");
    }

    @Test
    @DisplayName("getCurrentUserProfile powinien pobrać nazwę użytkownika z kontekstu bezpieczeństwa")
    void getCurrentUserProfile_shouldGetUsernameFromSecurityContext() {
        // given
        Authentication customAuth = new UsernamePasswordAuthenticationToken(
                "customUser",
                null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(customAuth);
        when(userService.getUserByLogin("customUser")).thenReturn(userDTO);

        // when
        ResponseEntity<UserDTO> response = userController.getCurrentUserProfile();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).getUserByLogin("customUser");
    }
}
