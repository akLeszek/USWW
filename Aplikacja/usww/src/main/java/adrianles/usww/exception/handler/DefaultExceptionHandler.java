package adrianles.usww.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class DefaultExceptionHandler extends AbstractExceptionHandler<Exception> {

    public DefaultExceptionHandler() {
        super(Exception.class);
    }

    @Override
    protected ResponseEntity<?> handleException(Exception exception) {
        exception.printStackTrace();
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
