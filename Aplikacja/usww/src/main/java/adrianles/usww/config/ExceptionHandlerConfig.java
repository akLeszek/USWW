package adrianles.usww.config;

import adrianles.usww.exception.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionHandlerConfig {

    @Bean
    public ExceptionHandler<?> exceptionHandlerChain() {
        DefaultExceptionHandler defaultHandler = new DefaultExceptionHandler();
        ValidationExceptionHandler validationHandler = new ValidationExceptionHandler();
        ResourceNotFoundExceptionHandler resourceNotFoundHandler = new ResourceNotFoundExceptionHandler();
        IllegalArgumentExceptionHandler illegalArgumentHandler = new IllegalArgumentExceptionHandler();
        LockedExceptionHandler lockedExceptionHandler = new LockedExceptionHandler();
        UnauthorizedAccessExceptionHandler unauthorizedAccessExceptionHandler = new UnauthorizedAccessExceptionHandler();

        resourceNotFoundHandler.setNext(validationHandler);
        validationHandler.setNext(illegalArgumentHandler);
        illegalArgumentHandler.setNext(lockedExceptionHandler);
        lockedExceptionHandler.setNext(defaultHandler);
        unauthorizedAccessExceptionHandler.setNext(unauthorizedAccessExceptionHandler);

        return resourceNotFoundHandler;
    }
}
