package adrianles.usww.unit.exception;

import adrianles.usww.api.dto.response.ErrorResponse;
import adrianles.usww.exception.handler.AbstractExceptionHandler;
import adrianles.usww.exception.handler.ExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AbstractExceptionHandlerTest {

    private TestExceptionHandler handler;
    private ExceptionHandler<Exception> nextHandler;

    // Klasa testowa implementująca AbstractExceptionHandler dla celów testowych
    private static class TestExceptionHandler extends AbstractExceptionHandler<IllegalArgumentException> {
        public TestExceptionHandler() {
            super(IllegalArgumentException.class);
        }

        @Override
        protected ResponseEntity<?> handleException(IllegalArgumentException exception) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        handler = new TestExceptionHandler();
        nextHandler = mock(ExceptionHandler.class);
        handler.setNext(nextHandler);
    }

    @Test
    @DisplayName("canHandle powinien zwrócić true dla obsługiwanego typu wyjątku")
    void canHandle_shouldReturnTrueForSupportedExceptionType() {
        // Given
        Exception exception = new IllegalArgumentException("Test exception");

        // When
        boolean result = handler.canHandle(exception);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("canHandle powinien zwrócić false dla nieobsługiwanego typu wyjątku")
    void canHandle_shouldReturnFalseForUnsupportedExceptionType() {
        // Given
        Exception exception = new NullPointerException("Test exception");

        // When
        boolean result = handler.canHandle(exception);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("handle powinien zwrócić odpowiedź błędu dla obsługiwanego typu wyjątku")
    void handle_shouldReturnErrorResponseForSupportedExceptionType() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Test message");

        // When
        ResponseEntity<?> response = handler.handle(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo("Test message");
    }

    @Test
    @DisplayName("handle powinien delegować do następnego handlera dla nieobsługiwanego typu wyjątku")
    void handle_shouldDelegateToNextHandlerForUnsupportedExceptionType() {
        // Given
        NullPointerException exception = new NullPointerException("Test exception");
        when(nextHandler.handle(exception)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // When
        handler.handle(exception);

        // Then
        verify(nextHandler).handle(exception);
    }

    @Test
    @DisplayName("handle powinien zwrócić domyślną odpowiedź błędu, gdy nie ma następnego handlera")
    void handle_shouldReturnDefaultErrorResponseWhenNoNextHandler() {
        // Given
        handler.setNext(null);
        NullPointerException exception = new NullPointerException("Test exception");

        // When
        ResponseEntity<?> response = handler.handle(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).contains("Unsupported exception");
    }
}
