package adrianles.usww.unit.exception;

import adrianles.usww.api.dto.response.ErrorResponse;
import adrianles.usww.exception.handler.ExceptionHandler;
import adrianles.usww.exception.handler.IllegalArgumentExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IllegalArgumentExceptionHandlerTest {

    private IllegalArgumentExceptionHandler handler;
    private ExceptionHandler<Exception> nextHandler;

    @BeforeEach
    void setUp() {
        handler = new IllegalArgumentExceptionHandler();
        nextHandler = mock(ExceptionHandler.class);
        handler.setNext(nextHandler);
    }

    @Test
    @DisplayName("canHandle powinien zwrócić true dla IllegalArgumentException")
    void canHandle_shouldReturnTrueForIllegalArgumentException() {
        // Given
        Exception exception = new IllegalArgumentException("Invalid argument");

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
    @DisplayName("handle powinien zwrócić odpowiedź HTTP 400 dla IllegalArgumentException")
    void handle_shouldReturnBadRequestResponseForIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // When
        ResponseEntity<?> response = handler.handle(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo("Invalid argument");
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
