package adrianles.usww.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"adrianles.usww.domain.entity"})
@EnableJpaRepositories(basePackages = {"adrianles.usww.domain.repository"})
@EnableTransactionManagement
public class TestConfig {
}
