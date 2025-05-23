package adrianles.usww.unit.security;

import adrianles.usww.security.jwt.JwtAuthenticationFilter;
import adrianles.usww.security.jwt.JwtUtil;
import adrianles.usww.security.userdetails.UserDetailsServiceImpl;
import adrianles.usww.service.impl.TokenCacheService;
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
    private TokenCacheService tokenCacheService;

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
        String token = "validToken";
        String username = "testUser";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(tokenCacheService.isTokenActive(username, token)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);

        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(tokenCacheService).isTokenActive(username, token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtUtil).validateToken(token, username);
    }

    @Test
    @DisplayName("Filtr powinien przejść dalej bez autoryzacji, gdy nagłówek Authorization jest pusty")
    void shouldContinueFilterChainWhenAuthorizationHeaderIsEmpty() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService, jwtUtil, tokenCacheService);
    }

    @Test
    @DisplayName("Filtr powinien przejść dalej, gdy token jest nieprawidłowy")
    void shouldContinueFilterChainWhenTokenIsInvalid() throws ServletException, IOException {
        String token = "invalidToken";
        String username = "testUser";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(tokenCacheService.isTokenActive(username, token)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(false);

        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(tokenCacheService).isTokenActive(username, token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtUtil).validateToken(token, username);
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Filtr powinien zwrócić 401, gdy token nie jest aktywny w cache")
    void shouldReturn401WhenTokenIsNotActiveInCache() throws ServletException, IOException {
        String token = "inactiveToken";
        String username = "testUser";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(tokenCacheService.isTokenActive(username, token)).thenReturn(false);

        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(tokenCacheService).isTokenActive(username, token);
        verifyNoInteractions(userDetailsService);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verifyNoMoreInteractions(filterChain);
    }

    @Test
    @DisplayName("Filtr powinien przejść dalej, gdy nagłówek nie zaczyna się od Bearer")
    void shouldContinueFilterChainWhenHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic sometoken");

        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService, jwtUtil, tokenCacheService);
    }

    @Test
    @DisplayName("Filtr powinien przejść dalej, gdy użytkownik jest już uwierzytelniony")
    void shouldContinueFilterChainWhenUserAlreadyAuthenticated() throws ServletException, IOException {
        String token = "validToken";
        String username = "testUser";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);

        SecurityContextHolder.getContext().setAuthentication(mock(org.springframework.security.core.Authentication.class));

        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "doFilterInternal", request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenCacheService, userDetailsService);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
    }
}
