package adrianles.usww;

import adrianles.usww.configuration.DataSourceConfig;
import adrianles.usww.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.repository.CrudRepository;

@SpringBootApplication
public class UswwApplication {

    public static void main(String[] args) {
        SpringApplication.run(UswwApplication.class, args);
    }

}
