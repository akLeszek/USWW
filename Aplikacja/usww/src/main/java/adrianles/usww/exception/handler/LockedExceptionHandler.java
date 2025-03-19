package adrianles.usww.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;


public class LockedExceptionHandler extends AbstractExceptionHandler<LockedException> {

    public LockedExceptionHandler() {
        super(LockedException.class);
    }

    @Override
    protected ResponseEntity<?> handleException(LockedException exception) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }
}
