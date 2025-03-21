package adrianles.usww.unit.exception;

import adrianles.usww.api.dto.response.ErrorResponse;
import adrianles.usww.exception.handler.ExceptionHandler;
import adrianles.usww.exception.handler.LockedExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LockedExceptionHandlerTest {

    private LockedExceptionHandler handler;
    private ExceptionHandler<Exception> nextHandler;

    @BeforeEach
    void setUp() {
        handler = new LockedExceptionHandler();
        nextHandler = mock(ExceptionHandler.class);
        handler.setNext(nextHandler);
    }

    @Test
    @DisplayName("canHandle powinien zwrócić true dla LockedException")
    void canHandle_shouldReturnTrueForLockedException() {
        // Given
        Exception exception = new LockedException("Account is locked");

        // When
        boolean result = handler.canHandle(exception);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("canHandle powinien zwrócić false dla innych wyjątków")
    void canHandle_shouldReturnFalseForOtherExceptions() {
        // Given
        Exception exception = new RuntimeException("Some other exception");

        // When
        boolean result = handler.canHandle(exception);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("handle powinien zwrócić odpowiedź HTTP 401 dla LockedException")
    void handle_shouldReturnUnauthorizedResponseForLockedException() {
        // Given
        LockedException exception = new LockedException("Account is locked");

        // When
        ResponseEntity<?> response = handler.handle(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo("Account is locked");
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("handle powinien delegować do następnego handlera dla innych wyjątków")
    void handle_shouldDelegateToNextHandlerForOtherExceptions() {
        // Given
        Exception exception = new RuntimeException("Some other exception");
        when(nextHandler.handle(exception)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When
        handler.handle(exception);

        // Then
        verify(nextHandler).handle(exception);
    }
}
