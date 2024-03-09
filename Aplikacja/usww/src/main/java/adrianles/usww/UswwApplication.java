package adrianles.usww;

import adrianles.usww.configuration.DataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
public class UswwApplication {

    public static void main(String[] args) {
        SpringApplication.run(UswwApplication.class, args);
    }

}
