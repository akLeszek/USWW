package adrianles.usww.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class NumberFormatExceptionHandler extends AbstractExceptionHandler<NumberFormatException> {

    public NumberFormatExceptionHandler() {
        super(NumberFormatException.class);
    }

    @Override
    protected ResponseEntity<?> handleException(NumberFormatException exception) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Incorrect number format: " + exception.getMessage());
    }
}
