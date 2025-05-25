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
import adrianles.usww.service.facade.UserService;
import adrianles.usww.service.impl.TokenCacheService;
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
import static org.mockito.ArgumentMatchers.eq;
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
    private TokenCacheService tokenCacheService;

    @Mock
    private Authentication authentication;

    @Mock
    private ExtendedUserDetails userDetails;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private AuthRequest authRequest;
    private PasswordChangeRequestDTO passwordChangeRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        userService.updateLastLoginTime("testuser");

        passwordChangeRequest = new PasswordChangeRequestDTO();
        passwordChangeRequest.setCurrentPassword("oldPassword");
        passwordChangeRequest.setNewPassword("newPassword");
    }

    @Test
    @DisplayName("Login - Pierwszy login wymaga zmiany hasła")
    void loginShouldRequirePasswordChangeOnFirstLogin() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadExtendedUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.isFirstLogin()).thenReturn(true);
        when(userDetails.getUserId()).thenReturn(1);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtUtil.generateToken("testuser")).thenReturn("testToken");

        ResponseEntity<?> response = authController.login(authRequest);

        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody).containsKey("requirePasswordChange");
        assertThat(responseBody.get("requirePasswordChange")).isEqualTo(true);
        assertThat(responseBody.get("token")).isEqualTo("testToken");
        assertThat(responseBody.get("userId")).isEqualTo(1);

        verify(jwtUtil).generateToken("testuser");
        verifyNoInteractions(tokenCacheService);
    }

    @Test
    @DisplayName("Login - Standardowe logowanie")
    void loginShouldReturnTokenOnSuccessfulLogin() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadExtendedUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.isFirstLogin()).thenReturn(false);
        when(userDetails.getUserId()).thenReturn(1);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtUtil.generateToken("testuser")).thenReturn("testToken");

        ResponseEntity<?> response = authController.login(authRequest);

        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertThat(authResponse.getUserId()).isEqualTo(1);
        assertThat(authResponse.getUsername()).isEqualTo("testuser");
        assertThat(authResponse.getToken()).isEqualTo("testToken");

        verify(tokenCacheService).setActiveToken("testuser", "testToken");
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    @DisplayName("Change Password - Sukces")
    void changePasswordShouldSucceed() {
        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(1);
        updatedUser.setLogin("testuser");

        when(userPasswordService.changePassword(eq(1), eq("oldPassword"), eq("newPassword")))
                .thenReturn(updatedUser);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtUtil.generateToken("testuser")).thenReturn("newToken");

        ResponseEntity<?> response = authController.changePassword(1, passwordChangeRequest);

        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody.get("message")).isEqualTo("Password changed successfully");
        assertThat(responseBody.get("token")).isEqualTo("newToken");

        verify(userPasswordService).changePassword(1, "oldPassword", "newPassword");
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    @DisplayName("Login - Logout powinien usunąć token z cache")
    void logoutShouldRemoveTokenFromCache() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("testuser");

        ResponseEntity<?> response = authController.logout(mockAuth);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(tokenCacheService).removeToken("testuser");
    }

    @Test
    @DisplayName("Login - Logout bez uwierzytelnienia")
    void logoutShouldHandleNullAuthentication() {
        ResponseEntity<?> response = authController.logout(null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verifyNoInteractions(tokenCacheService);
    }

    @Test
    @DisplayName("Login - Błąd uwierzytelniania")
    void loginShouldHandleAuthenticationFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        try {
            authController.login(authRequest);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
        }

        verifyNoInteractions(tokenCacheService);
        verifyNoInteractions(jwtUtil);
    }
}
