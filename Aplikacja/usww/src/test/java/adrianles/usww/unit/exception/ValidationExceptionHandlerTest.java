package adrianles.usww.unit.exception;

import adrianles.usww.exception.handler.ExceptionHandler;
import adrianles.usww.exception.handler.ValidationExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ValidationExceptionHandlerTest {

    private ValidationExceptionHandler handler;
    private ExceptionHandler<Exception> nextHandler;

    @BeforeEach
    void setUp() {
        handler = new ValidationExceptionHandler();
        nextHandler = mock(ExceptionHandler.class);
        handler.setNext(nextHandler);
    }

    @Test
    @DisplayName("canHandle powinien zwrócić true dla MethodArgumentNotValidException")
    void canHandle_shouldReturnTrueForMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

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
    @DisplayName("handle powinien zwrócić mapę błędów walidacji dla MethodArgumentNotValidException")
    void handle_shouldReturnValidationErrorsMapForMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("object", "field1", "Error message 1");
        FieldError fieldError2 = new FieldError("object", "field2", "Error message 2");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // When
        ResponseEntity<?> response = handler.handle(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertThat(errors).containsEntry("field1", "Error message 1");
        assertThat(errors).containsEntry("field2", "Error message 2");
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
