package adrianles.usww.unit.security;

import adrianles.usww.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String USERNAME = "testuser";
    private static final String SECRET_KEY = "edenHazardTheLegend2012FrankieIsTheKing7";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    @DisplayName("Generowanie tokenu JWT dla użytkownika")
    void generateToken_shouldCreateValidToken() {
        // When
        String token = jwtUtil.generateToken(USERNAME);

        // Then
        assertNotNull(token);
        assertEquals(USERNAME, jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Ekstrakcja nazwy użytkownika z tokenu")
    void extractUsername_shouldReturnCorrectUsername() {
        // When
        String token = jwtUtil.generateToken(USERNAME);
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertEquals(USERNAME, extractedUsername);
    }

    @Test
    @DisplayName("Walidacja tokenu z poprawną nazwą użytkownika")
    void validateToken_shouldReturnTrueForValidToken() {
        // When
        String token = jwtUtil.generateToken(USERNAME);
        boolean isValid = jwtUtil.validateToken(token, USERNAME);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Walidacja tokenu z niepoprawną nazwą użytkownika")
    void validateToken_shouldReturnFalseForInvalidUsername() {
        // When
        String token = jwtUtil.generateToken(USERNAME);
        boolean isValid = jwtUtil.validateToken(token, "differentuser");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Sprawdzenie wygaśnięcia tokenu")
    void isTokenExpired_shouldReturnFalseForValidToken() {
        // When
        String token = jwtUtil.generateToken(USERNAME);
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Sprawdzenie dat tokenu")
    void token_shouldHaveCorrectDates() {
        // When
        String token = jwtUtil.generateToken(USERNAME);
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Then
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}
