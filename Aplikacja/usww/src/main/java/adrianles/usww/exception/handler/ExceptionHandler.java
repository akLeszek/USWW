package adrianles.usww.exception.handler;

import org.springframework.http.ResponseEntity;

public interface ExceptionHandler<T extends Exception> {

    boolean canHandle(T exception);

    ResponseEntity<?> handle(Exception exception);

    void setNext(ExceptionHandler<? extends Exception> next);

}
