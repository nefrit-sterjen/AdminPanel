package ru.mail.vladislav150199;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;


public class RootAdminPanel {

    public static void main(String[] args) throws ClassNotFoundException, IOException {

        if (args.length != 0) {

            String choice = args[0];
            //Считываение данных для подключения из файла config.ini
            ConnectionData data = new ConnectionData();
            //Подключение к БД
            Class.forName("com.mysql.jdbc.Driver");
            try {

                Connection connection = DriverManager.getConnection(data.getConnectionUrl(), data.getUserName(), data.getPassword());
                Statement statement = connection.createStatement();


                switch (choice) {

                    //Смена пароля суперпользователя
                    case "change_pass":
                        BufferedReader passwordReader = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("Enter new password: ");
                        String password = passwordReader.readLine();
                        System.out.print("Repeat password: ");
                        String repeat = passwordReader.readLine();

                        passwordReader.close();

                        if (password.equals(repeat)) {
                            Bcrypt bcrypt = new Bcrypt(password);

                            //проверка наличия root в таблице
                            ResultSet resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM accounts WHERE login = 'root')");
                            resultSet.next();
                            int availability = resultSet.getInt(1);

                            //root пользователь есть в таблице
                            if (availability != 0) {
                                statement.executeUpdate("UPDATE accounts SET password = '" + bcrypt.getHashedPassword() + "' WHERE login = 'root' ");
                                System.out.println("Password was updated!");
                            }
                            //root пользователя нет в таблице
                            else {
                                System.out.println("Root user is not found!");

                                //Условие когда флаг на создание нового root пользователя установлен
                                if (data.isCreateRoot()) {

                                    statement.executeUpdate("INSERT INTO accounts ( login, password) \n" +
                                            "\n" +
                                            "VALUES\n" +
                                            "\n" +
                                            " ('root', '" + bcrypt.getHashedPassword() + "')");
                                    System.out.println("Root user created!");

                                }
                                //Условие когда флаг на создание нового root пользователя не установлен
                                else {
                                    System.out.println("Unable to create root! The create flag is not set.");

                                }
                            }


                        } else {
                            System.out.println("Your password was not changed. New password and repeated new password did not match.");
                        }

                        statement.close();
                        connection.close();
                        break;

                    //Смена ключа лицензии
                    case "change_license":
                        BufferedReader licenseReader = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("Enter file path: ");
                        String path = licenseReader.readLine();
                        licenseReader.close();

                        //Считываение пароля из файла

                        String line = null;
                        String publicKey = null;
                        String privateKey = null;

                        BufferedReader fileReader = new BufferedReader(new FileReader(path));

                        while ((line = fileReader.readLine()) != null) {

                            if (line.contains("Public key=")) {
                                publicKey = line.substring(line.indexOf('=') + 1);
                                publicKey = publicKey.replace(" ", "");
                            } else if (line.contains("Private key=")) {
                                privateKey = line.substring(line.indexOf('=') + 1);
                                privateKey = privateKey.replace(" ", "");
                            }

                        }
                        fileReader.close();


                        statement.executeUpdate("UPDATE settings SET value = '" + publicKey + "' WHERE name='public_key'");
                        statement.executeUpdate("UPDATE settings SET value = '" + privateKey + "' WHERE name='private_key'");
                        System.out.println("License was updated!");

                        statement.close();
                        connection.close();
                        break;


                    default:
                        System.out.println("Unknown command!");
                        statement.close();
                        connection.close();
                        break;

                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Connection error!");
                System.out.println(e.getMessage());

            }

        }
    }
}
