package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AppConfig {
    private static final Properties PROPERTIES = loadProperties();

    private AppConfig() {
    }

    public static String get(String property, String environmentVariable, String defaultValue) {
        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return PROPERTIES.getProperty(property, defaultValue).trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        Path configPath = Path.of("config.properties");
        if (Files.isRegularFile(configPath)) {
            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new IllegalStateException("Could not read config.properties", exception);
            }
        }
        return properties;
    }
}