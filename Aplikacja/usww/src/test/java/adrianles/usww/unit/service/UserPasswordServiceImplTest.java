package adrianles.usww.unit.service;

import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.api.mapper.UserMapper;
import adrianles.usww.domain.entity.User;
import adrianles.usww.domain.repository.UserRepository;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.service.impl.UserPasswordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPasswordServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserPasswordServiceImpl userPasswordService;

    private User testUser;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setLogin("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setFirstLogin(true);

        userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setLogin("testuser");
        userDTO.setFirstLogin(false);
    }

    @Test
    @DisplayName("changePassword powinien zmienić hasło użytkownika gdy aktualne hasło jest poprawne")
    void changePassword_shouldChangePasswordWhenCurrentPasswordIsCorrect() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

        // when
        UserDTO result = userPasswordService.changePassword(1, "currentPassword", "newPassword");

        // then
        assertThat(result).isNotNull();
        assertThat(result.isFirstLogin()).isFalse();

        verify(userRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("changePassword powinien wyrzucić wyjątek gdy aktualne hasło jest niepoprawne")
    void changePassword_shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                userPasswordService.changePassword(1, "wrongPassword", "newPassword")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Aktualne hasło jest nieprawidłowe");

        verify(userRepository).findById(1);
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword powinien wyrzucić wyjątek gdy użytkownik nie istnieje")
    void changePassword_shouldThrowExceptionWhenUserDoesNotExist() {
        // given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                userPasswordService.changePassword(999, "currentPassword", "newPassword")
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Użytkownik o id 999 nie istnieje");

        verify(userRepository).findById(999);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword powinien zaktualizować flagi firstLogin i lastLogin")
    void changePassword_shouldUpdateFirstLoginAndLastLoginFlags() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

        // when
        userPasswordService.changePassword(1, "currentPassword", "newPassword");

        // then
        verify(userRepository).save(testUser);
        assertThat(testUser.isFirstLogin()).isFalse();
        assertThat(testUser.getLastLogin()).isNotNull();
    }

    @Test
    @DisplayName("resetPassword powinien zresetować hasło użytkownika do loginu")
    void resetPassword_shouldResetPasswordToUserLogin() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("testuser")).thenReturn("encodedDefaultPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

        // when
        UserDTO result = userPasswordService.resetPassword(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGeneratedPassword()).isEqualTo("testuser");

        verify(userRepository).findById(1);
        verify(passwordEncoder).encode("testuser");
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("resetPassword powinien ustawić firstLogin na true")
    void resetPassword_shouldSetFirstLoginToTrue() {
        // given
        testUser.setFirstLogin(false);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedDefaultPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

        // when
        userPasswordService.resetPassword(1);

        // then
        verify(userRepository).save(testUser);
        assertThat(testUser.isFirstLogin()).isTrue();
    }

    @Test
    @DisplayName("resetPassword powinien wyrzucić wyjątek gdy użytkownik nie istnieje")
    void resetPassword_shouldThrowExceptionWhenUserDoesNotExist() {
        // given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                userPasswordService.resetPassword(999)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Użytkownik o id 999 nie istnieje");

        verify(userRepository).findById(999);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
