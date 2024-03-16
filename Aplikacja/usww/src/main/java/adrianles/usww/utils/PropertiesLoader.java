package adrianles.usww.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesLoader {
    private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
    private static Properties properties = new Properties();

    public static Properties getPropertiesFromResources(String propertiesFileName) throws IOException {
        Path propertiesPath = Path.of(ClassLoader.getSystemResource(propertiesFileName).getPath());
        loadPropertiesFromFile(propertiesPath.toFile());
        return properties;
    }

    private static void loadPropertiesFromFile(File propertiesFile) throws IOException {
        try {
            InputStream propertiesStream = new FileInputStream(propertiesFile);
            properties.load(propertiesStream);
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }
}
