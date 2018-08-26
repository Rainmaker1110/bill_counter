package facturas.io;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class for read properties from a file. Can be extended for support more types.
 *
 * @author Hector Enrique Diaz Hernandez
 */
public class PropertiesReader {
    private static Logger log; // LOGGER

    private Properties properties;

    static {
        log = Logger.getLogger(PropertiesReader.class.getName());
    }

    public PropertiesReader() {
        properties = null;
    }

    /**
     * Attempts to read a properties file.
     *
     * @param fileName the properties filename
     */
    public boolean readFromFile(String fileName) {
        try {
            fileName = getClass().getClassLoader().getResource(fileName).getPath();

            log.info("Reading properties from: " + fileName);

            properties = new Properties();

            properties.load(new FileInputStream(fileName));

            properties.forEach((key, value) -> log.debug(key + ": " + value));

            return true;
        } catch (IOException e) {
            log.error("Error while reading \"" + fileName + "\", please set properties to default");
            e.printStackTrace();

            return false;
        } catch (NullPointerException e) {
            log.error("File \"" + fileName + "\" not found, please set properties to default");
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Returns a string property
     *
     * @param key the property key
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Returns a integer property
     *
     * @param key the property key
     */
    public int getIntegerProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
