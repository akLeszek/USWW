package adrianles.usww.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class MultipartConfig {

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final long MAX_REQUEST_SIZE = 10 * 1024 * 1024; // 10MB

    public static final String[] ALLOWED_CONTENT_TYPES = {
            "application/pdf",
            "image/jpeg",
            "image/png"
    };

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(MAX_FILE_SIZE));
        factory.setMaxRequestSize(DataSize.ofBytes(MAX_REQUEST_SIZE));
        return factory.createMultipartConfig();
    }
}
