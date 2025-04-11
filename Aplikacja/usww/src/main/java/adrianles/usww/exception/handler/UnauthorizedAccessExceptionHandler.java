package adrianles.usww.exception.handler;

import adrianles.usww.exception.UnauthorizedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UnauthorizedAccessExceptionHandler extends AbstractExceptionHandler<UnauthorizedAccessException> {

    public UnauthorizedAccessExceptionHandler() {
        super(UnauthorizedAccessException.class);
    }

    @Override
    protected ResponseEntity<?> handleException(UnauthorizedAccessException exception) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }
}
