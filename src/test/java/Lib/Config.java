package Lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/test/config.properties")) {
            // Загружаем файл
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("api.base.url", "https://playground.learnqa.ru/api/");
    }

}
