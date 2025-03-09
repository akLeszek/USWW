package adrianles.usww.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class IllegalArgumentExceptionHandler extends AbstractExceptionHandler<IllegalArgumentException> {

    public IllegalArgumentExceptionHandler() {
        super(IllegalArgumentException.class);
    }

    @Override
    protected ResponseEntity<?> handleException(IllegalArgumentException exception) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
