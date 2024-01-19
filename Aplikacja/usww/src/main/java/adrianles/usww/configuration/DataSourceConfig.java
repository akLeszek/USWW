package adrianles.usww.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        Properties dataSourceProperties = getDataSourceProperties();
        dataSourceBuilder.driverClassName("");
        dataSourceBuilder.url(dataSourceProperties.getProperty("url"));
        dataSourceBuilder.username(dataSourceProperties.getProperty("user"));
        dataSourceBuilder.password(dataSourceProperties.getProperty("password"));
        return dataSourceBuilder.build();
    }

    private Properties getDataSourceProperties() {
        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("url", getUrlProperty(dataSourceProperties));
        return dataSourceProperties;
    }

    private String getUrlProperty(Properties dataSourceProperties) {
        StringBuilder stringBuilder = new StringBuilder();
        String host = dataSourceProperties.getProperty("host");
        String port = dataSourceProperties.getProperty("port");
        String database = dataSourceProperties.getProperty("dbName");

        stringBuilder.append("jdbc:sqlserver://");
        stringBuilder.append(host + ":" + port);
        stringBuilder.append(";database=");
        stringBuilder.append(database);
        return stringBuilder.toString();
    }
}
