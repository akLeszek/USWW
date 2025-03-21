package adrianles.usww.unit.exception;

import adrianles.usww.api.dto.response.ErrorResponse;
import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.exception.handler.ExceptionHandler;
import adrianles.usww.exception.handler.ResourceNotFoundExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ResourceNotFoundExceptionHandlerTest {

    private ResourceNotFoundExceptionHandler handler;
    private ExceptionHandler<Exception> nextHandler;

    @BeforeEach
    void setUp() {
        handler = new ResourceNotFoundExceptionHandler();
        nextHandler = mock(ExceptionHandler.class);
        handler.setNext(nextHandler);
    }

    @Test
    @DisplayName("canHandle powinien zwrócić true dla ResourceNotFoundException")
    void canHandle_shouldReturnTrueForResourceNotFoundException() {
        // Given
        Exception exception = new ResourceNotFoundException("Resource not found");

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
    @DisplayName("handle powinien zwrócić odpowiedź HTTP 404 dla ResourceNotFoundException")
    void handle_shouldReturnNotFoundResponseForResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // When
        ResponseEntity<?> response = handler.handle(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo("Resource not found");
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
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
