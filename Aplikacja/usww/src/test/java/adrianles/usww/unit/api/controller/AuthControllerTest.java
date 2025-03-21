package adrianles.usww.unit.api.controller;

import adrianles.usww.api.controller.common.AuthController;
import adrianles.usww.api.dto.UserDTO;
import adrianles.usww.api.dto.request.AuthRequest;
import adrianles.usww.api.dto.request.PasswordChangeRequestDTO;
import adrianles.usww.api.dto.response.AuthResponse;
import adrianles.usww.security.jwt.JwtUtil;
import adrianles.usww.security.userdetails.ExtendedUserDetails;
import adrianles.usww.security.userdetails.UserDetailsServiceImpl;
import adrianles.usww.service.facade.UserPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserPasswordService userPasswordService;

    @Mock
    private Authentication authentication;

    @Mock
    private ExtendedUserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    private AuthRequest authRequest;
    private PasswordChangeRequestDTO passwordChangeRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        passwordChangeRequest = new PasswordChangeRequestDTO();
        passwordChangeRequest.setCurrentPassword("oldPassword");
        passwordChangeRequest.setNewPassword("newPassword");
    }

    @Test
    @DisplayName("Login - Pierwszy login wymaga zmiany hasła")
    void loginShouldRequirePasswordChangeOnFirstLogin() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadExtendedUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.isFirstLogin()).thenReturn(true);
        when(userDetails.getUserId()).thenReturn(1);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtUtil.generateToken("testuser")).thenReturn("testToken");

        // when
        ResponseEntity<?> response = authController.login(authRequest);

        // then
        assertThat(response.getBody()).isInstanceOf(java.util.Map.class);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).containsKey("requirePasswordChange");
        assertThat(responseBody.get("requirePasswordChange")).isEqualTo(true);
    }

    @Test
    @DisplayName("Login - Standardowe logowanie")
    void loginShouldReturnTokenOnSuccessfulLogin() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadExtendedUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.isFirstLogin()).thenReturn(false);
        when(userDetails.getUserId()).thenReturn(1);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtUtil.generateToken("testuser")).thenReturn("testToken");

        // when
        ResponseEntity<?> response = authController.login(authRequest);

        // then
        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertThat(authResponse.getUserId()).isEqualTo(1);
        assertThat(authResponse.getToken()).isEqualTo("testToken");
    }

    @Test
    @DisplayName("Change Password - Sukces")
    void changePasswordShouldSucceed() {
        // given
        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(1);
        updatedUser.setLogin("testuser");

        when(userPasswordService.changePassword(eq(1), eq("oldPassword"), eq("newPassword")))
                .thenReturn(updatedUser);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtUtil.generateToken("testuser")).thenReturn("newToken");

        // when
        ResponseEntity<?> response = authController.changePassword(1, passwordChangeRequest);

        // then
        assertThat(response.getBody()).isInstanceOf(java.util.Map.class);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody.get("message")).isEqualTo("Password changed successfully");
        assertThat(responseBody.get("token")).isEqualTo("newToken");

        // Dodatkowe weryfikacje wywołań
        verify(userPasswordService).changePassword(1, "oldPassword", "newPassword");
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).generateToken("testuser");
    }
}
