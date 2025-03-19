package adrianles.usww.exception.handler;

import adrianles.usww.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ResourceNotFoundExceptionHandler extends AbstractExceptionHandler<ResourceNotFoundException> {
    public ResourceNotFoundExceptionHandler() {
        super(ResourceNotFoundException.class);
    }

    @Override
    protected ResponseEntity<?> handleException(ResourceNotFoundException exception) {
        return createErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
