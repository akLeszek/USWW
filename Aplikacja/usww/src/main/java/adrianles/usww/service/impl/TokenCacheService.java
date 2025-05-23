package adrianles.usww.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCacheService {

    private final CacheManager cacheManager;

    public void setActiveToken(String username, String token) {
        Cache cache = cacheManager.getCache("activeUserTokens");
        if (cache != null) {
            cache.put(username, token);
        }
    }

    public boolean isTokenActive(String username, String token) {
        Cache cache = cacheManager.getCache("activeUserTokens");
        if (cache != null) {
            String activeToken = cache.get(username, String.class);
            return token.equals(activeToken);
        }
        return false;
    }

    public void removeToken(String username) {
        Cache cache = cacheManager.getCache("activeUserTokens");
        if (cache != null) {
            cache.evict(username);
        }
    }
}
