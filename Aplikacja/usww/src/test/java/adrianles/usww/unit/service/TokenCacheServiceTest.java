package adrianles.usww.unit.service;

import adrianles.usww.service.impl.TokenCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenCacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private TokenCacheService tokenCacheService;

    @BeforeEach
    void setUp() {
        when(cacheManager.getCache("activeUserTokens")).thenReturn(cache);
    }

    @Test
    @DisplayName("Powinien ustawić aktywny token dla użytkownika")
    void shouldSetActiveTokenForUser() {
        String username = "testUser";
        String token = "testToken";

        tokenCacheService.setActiveToken(username, token);

        verify(cache).put(username, token);
    }

    @Test
    @DisplayName("Powinien zwrócić true, gdy token jest aktywny")
    void shouldReturnTrueWhenTokenIsActive() {
        String username = "testUser";
        String token = "testToken";

        when(cache.get(username, String.class)).thenReturn(token);

        boolean result = tokenCacheService.isTokenActive(username, token);

        assertTrue(result);
        verify(cache).get(username, String.class);
    }

    @Test
    @DisplayName("Powinien zwrócić false, gdy token nie jest aktywny")
    void shouldReturnFalseWhenTokenIsNotActive() {
        String username = "testUser";
        String token = "testToken";
        String differentToken = "differentToken";

        when(cache.get(username, String.class)).thenReturn(differentToken);

        boolean result = tokenCacheService.isTokenActive(username, token);

        assertFalse(result);
        verify(cache).get(username, String.class);
    }

    @Test
    @DisplayName("Powinien zwrócić false, gdy brak tokena w cache")
    void shouldReturnFalseWhenNoTokenInCache() {
        String username = "testUser";
        String token = "testToken";

        when(cache.get(username, String.class)).thenReturn(null);

        boolean result = tokenCacheService.isTokenActive(username, token);

        assertFalse(result);
        verify(cache).get(username, String.class);
    }

    @Test
    @DisplayName("Powinien usunąć token z cache")
    void shouldRemoveTokenFromCache() {
        String username = "testUser";

        tokenCacheService.removeToken(username);

        verify(cache).evict(username);
    }

    @Test
    @DisplayName("Powinien obsłużyć brak cache gracefully")
    void shouldHandleMissingCacheGracefully() {
        when(cacheManager.getCache("activeUserTokens")).thenReturn(null);

        assertDoesNotThrow(() -> {
            tokenCacheService.setActiveToken("user", "token");
            tokenCacheService.removeToken("user");
            boolean result = tokenCacheService.isTokenActive("user", "token");
            assertFalse(result);
        });
    }
}
