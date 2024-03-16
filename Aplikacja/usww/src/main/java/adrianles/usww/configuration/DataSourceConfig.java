package adrianles.usww.configuration;

import adrianles.usww.utils.PropertiesLoader;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class DataSourceConfig {

    public DataSource getDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        Properties dataSourceProperties = getDataSourceProperties();
        dataSourceBuilder.driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSourceBuilder.url(dataSourceProperties.getProperty("url"));
        dataSourceBuilder.username(dataSourceProperties.getProperty("user"));
        dataSourceBuilder.password(dataSourceProperties.getProperty("password"));
        return dataSourceBuilder.build();
    }

    private Properties getDataSourceProperties() {
        Properties dataSourceProperties;
        try {
            dataSourceProperties = PropertiesLoader.getPropertiesFromResources("mainDataSource.properties");
            dataSourceProperties.setProperty("url", getUrlProperty(dataSourceProperties));
            return dataSourceProperties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUrlProperty(Properties dataSourceProperties) {
        StringBuilder stringBuilder = new StringBuilder();
        String host = dataSourceProperties.getProperty("host");
        String port = dataSourceProperties.getProperty("port");
        String database = dataSourceProperties.getProperty("dbName");

        stringBuilder.append("jdbc:sqlserver://");
        stringBuilder.append(host).append(":").append(port);
        stringBuilder.append(";database=");
        stringBuilder.append(database);
        return stringBuilder.toString();
    }
}
