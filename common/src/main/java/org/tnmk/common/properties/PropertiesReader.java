package org.tnmk.common.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class PropertiesReader {
    public static final Optional<Properties> PROJECT_PROPERTIES = readPropertiesFromClassPathIfPossible("/build.properties");

    public static Optional<Properties> readPropertiesFromClassPathIfPossible(String filePath) {
        try {
            Properties properties = readPropertiesFromClassPath(filePath);
            return Optional.of(properties);
        } catch (IllegalStateException ex) {
            return Optional.empty();
        }
    }

    public static Properties readPropertiesFromClassPath(String filePath) throws IllegalStateException {
        try (InputStream input = PropertiesReader.class.getResourceAsStream(filePath)) {
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load properties file " + filePath);
        }
    }
}
