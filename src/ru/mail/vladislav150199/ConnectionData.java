package ru.mail.vladislav150199;

//Класс для чтения хранения данных для подключения к БД

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionData {
    private String userName;
    private String password;
    private String connectionUrl;
    private boolean createRoot;

    public ConnectionData() {
        Properties properties = new Properties();
        try (InputStream inputStream = this.getClass().getResourceAsStream("../../../resources/config.ini")) {

            properties.load(inputStream);

            this.userName = properties.getProperty("DB_USERNAME");
            this.password = properties.getProperty("DB_PASSWORD");
            this.connectionUrl = properties.getProperty("DB_URL");
            this.createRoot = Boolean.parseBoolean(properties.getProperty("DB_CREATE_ROOT"));

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public boolean isCreateRoot() {
        return createRoot;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }
}
