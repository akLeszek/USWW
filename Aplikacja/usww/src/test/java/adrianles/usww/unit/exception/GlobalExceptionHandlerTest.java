package adrianles.usww.unit.exception;

import adrianles.usww.exception.ResourceNotFoundException;
import adrianles.usww.exception.handler.ExceptionHandler;
import adrianles.usww.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private ExceptionHandler<Exception> exceptionHandler;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private ResourceNotFoundException resourceNotFoundException;
    private IllegalArgumentException illegalArgumentException;
    private LockedException lockedException;
    private Exception genericException;

    @BeforeEach
    void setUp() {
        resourceNotFoundException = new ResourceNotFoundException("Resource not found");
        illegalArgumentException = new IllegalArgumentException("Invalid argument");
        lockedException = new LockedException("Account is locked");
        genericException = new Exception("Generic error");
    }

    @Test
    @DisplayName("GlobalExceptionHandler powinien delegować obsługę ResourceNotFoundException")
    void shouldDelegateResourceNotFoundException() {
        // Given
        ResponseEntity<Object> expectedResponse = ResponseEntity.notFound().build();
        when(exceptionHandler.handle(resourceNotFoundException)).thenReturn((ResponseEntity) expectedResponse);

        // When
        ResponseEntity<?> actualResponse = globalExceptionHandler.handleAllExceptions(resourceNotFoundException);

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(exceptionHandler).handle(resourceNotFoundException);
    }

    @Test
    @DisplayName("GlobalExceptionHandler powinien delegować obsługę IllegalArgumentException")
    void shouldDelegateIllegalArgumentException() {
        // Given
        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().build();
        when(exceptionHandler.handle(illegalArgumentException)).thenReturn((ResponseEntity) expectedResponse);

        // When
        ResponseEntity<?> actualResponse = globalExceptionHandler.handleAllExceptions(illegalArgumentException);

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(exceptionHandler).handle(illegalArgumentException);
    }

    @Test
    @DisplayName("GlobalExceptionHandler powinien delegować obsługę LockedException")
    void shouldDelegateLockedException() {
        // Given
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        when(exceptionHandler.handle(lockedException)).thenReturn((ResponseEntity) expectedResponse);

        // When
        ResponseEntity<?> actualResponse = globalExceptionHandler.handleAllExceptions(lockedException);

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(exceptionHandler).handle(lockedException);
    }

    @Test
    @DisplayName("GlobalExceptionHandler powinien delegować obsługę ogólnego wyjątku")
    void shouldDelegateGenericException() {
        // Given
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(exceptionHandler.handle(genericException)).thenReturn((ResponseEntity) expectedResponse);

        // When
        ResponseEntity<?> actualResponse = globalExceptionHandler.handleAllExceptions(genericException);

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(exceptionHandler).handle(genericException);
    }
}
