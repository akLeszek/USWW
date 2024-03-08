package adrianles.usww.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceFileLoader {
    public static File getJsonFile(String fileName, ClassLoader classLoader) {
        URL jsonFileUrl = classLoader.getResource(fileName);
        File file = new File(jsonFileUrl.getFile());
        return file;
    }

    public static String getJsonFileAsString(String fileName, ClassLoader classLoader) throws IOException {
        File file = getJsonFile(fileName, classLoader);
        return new String(Files.readAllBytes(file.toPath()));
    }
}
