package io.bdrc.lib.OcflClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppProperties {
    private static Properties _getProperties() {

        if (_properties != null) {
            return _properties;
        }

        String userHome = System.getProperty("user.home");
        Path propFilePath = Paths.get( userHome, ".config/ao/ocfldemo.properties");

        try (FileInputStream fileInputStream = new FileInputStream(propFilePath.toFile())) {
            _properties = new Properties();
            _properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _properties;
    }

    private static Properties _properties;
    public static Properties getProperties() {
        return _getProperties();
    }
}
