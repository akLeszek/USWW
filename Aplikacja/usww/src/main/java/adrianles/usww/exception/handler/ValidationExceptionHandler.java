package adrianles.usww.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;


public class ValidationExceptionHandler extends AbstractExceptionHandler<MethodArgumentNotValidException> {

    public ValidationExceptionHandler() {
        super(MethodArgumentNotValidException.class);
    }

    @Override
    protected ResponseEntity<?> handleException(MethodArgumentNotValidException exception) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
