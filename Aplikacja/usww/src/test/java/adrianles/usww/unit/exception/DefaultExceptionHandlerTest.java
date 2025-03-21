package adrianles.usww.unit.exception;

import adrianles.usww.api.dto.response.ErrorResponse;
import adrianles.usww.exception.handler.DefaultExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultExceptionHandlerTest {

    private DefaultExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DefaultExceptionHandler();
    }

    @Test
    @DisplayName("canHandle powinien zwrócić true dla dowolnego wyjątku")
    void canHandle_shouldReturnTrueForAnyException() {
        // Given
        Exception exception = new RuntimeException("Test exception");

        // When
        boolean result = handler.canHandle(exception);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("handle powinien zwrócić odpowiedź HTTP 500 dla dowolnego wyjątku")
    void handle_shouldReturnInternalServerErrorResponseForAnyException() {
        // Given
        Exception exception = new RuntimeException("Test exception");

        // When
        ResponseEntity<?> response = handler.handle(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo("Test exception");
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("handle powinien zwrócić odpowiedź HTTP 500 nawet dla NullPointerException")
    void handle_shouldReturnInternalServerErrorResponseForNullPointerException() {
        // Given
        Exception exception = new NullPointerException("Null pointer");

        // When
        ResponseEntity<?> response = handler.handle(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo("Null pointer");
    }
}
