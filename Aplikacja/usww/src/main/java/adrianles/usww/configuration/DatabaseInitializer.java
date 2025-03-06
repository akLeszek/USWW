package adrianles.usww.configuration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseInitializer {
    private final DataSource dataSource;

    public DatabaseInitializer(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void runScripts() {
        System.out.println("Starting Database initialization...");
        try (Connection connection = dataSource.getConnection()) {
            executeScripts(connection);
        } catch (SQLException exception) {
            System.out.println("Database initialization failed!");
        }
    }

    private void executeScripts(final Connection connection) throws SQLException {
        executeScript(connection, "dbScripts/czysczenieBazy.sql");
        executeScript(connection, "dbScripts/dbSchema.sql");
        executeScript(connection, "dbScripts/data.sql");
    }

    private void executeScript(final Connection connection, final String sqlScript) {
        try {
            System.out.println("Executing script " + sqlScript);
            Resource resource = new ClassPathResource(sqlScript);
            String script = resource.getContentAsString(Charset.defaultCharset());
            connection.createStatement().execute(script);
        } catch (Exception exception) {
            System.err.println("Error while executing script " + sqlScript);
            exception.printStackTrace();
        }
    }
}
