package adrianles.usww.unit.security;

import adrianles.usww.security.jwt.JwtAuthenticationFilter;
import adrianles.usww.security.jwt.JwtUtil;
import adrianles.usww.security.userdetails.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Filtr powinien przetworzyć żądanie z prawidłowym tokenem JWT")
    void shouldProcessRequestWithValidJwtToken() throws ServletException, IOException {
        // Given
        String token = "validToken";
        String username = "testUser";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails.getUsername())).thenReturn(true);

        // When
        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtUtil).validateToken(token, userDetails.getUsername());
    }

    @Test
    @DisplayName("Filtr powinien przejść dalej bez autoryzacji, gdy nagłówek Authorization jest pusty")
    void shouldContinueFilterChainWhenAuthorizationHeaderIsEmpty() throws ServletException, IOException {
        // Given
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // When
        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService, jwtUtil);
    }

    @Test
    @DisplayName("Filtr powinien przejść dalej, gdy token jest nieprawidłowy")
    void shouldContinueFilterChainWhenTokenIsInvalid() throws ServletException, IOException {
        // Given
        String token = "invalidToken";
        String username = "testUser";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails.getUsername())).thenReturn(false);

        // When
        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtUtil).validateToken(token, userDetails.getUsername());
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
