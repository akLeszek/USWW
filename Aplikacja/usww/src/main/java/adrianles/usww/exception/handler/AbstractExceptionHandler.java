package adrianles.usww.exception.handler;


import adrianles.usww.api.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public abstract class AbstractExceptionHandler<T extends Exception> implements ExceptionHandler<T> {
    private ExceptionHandler<? extends Exception> nextHandler;
    private final Class<T> exceptionType;

    protected AbstractExceptionHandler(Class<T> exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public boolean canHandle(Exception exception) {
        return exceptionType.isInstance(exception);
    }

    @Override
    public ResponseEntity<?> handle(Exception exception) {
        if (canHandle(exception)) {
            return handleException(exceptionType.cast(exception));
        } else if (nextHandler != null) {
            return nextHandler.handle(exception);
        } else {
            return createErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unsupported exception: " + exception.getMessage()
            );
        }
    }

    @Override
    public void setNext(ExceptionHandler<? extends Exception> handler) {
        this.nextHandler = handler;
    }

    protected abstract ResponseEntity<?> handleException(T exception);

    protected ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), status.value(), message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
