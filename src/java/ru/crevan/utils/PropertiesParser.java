package java.ru.crevan.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesParser {
    private static final Logger logger = Logger.getLogger(PropertiesParser.class.getName());

    private final Properties properties = new Properties();
    private final File file;

    public PropertiesParser(final File file) {
        this.file = file;
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, Charset.defaultCharset())) {
            properties.load(isr);

        } catch (Exception ex) {
            logger.warning("[" + file.getName() + "] Error loading file: " + ex.getMessage());
        }
    }

    public PropertiesParser(final String name) {
        this(new File(name));
    }

    public String getString(final String key, final String defaultValue) {
        //todo
        return null;
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        //todo
        return false;
    }

    private String getValue(final String key) {
        final String value = properties.getProperty(key);
        return value == null ? null : value.trim();
    }
}
