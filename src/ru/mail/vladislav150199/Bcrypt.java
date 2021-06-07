package ru.mail.vladislav150199;

import org.springframework.security.crypto.bcrypt.BCrypt;

//Класс для шифрования пароля
public class Bcrypt {
    private String password;

    private String hashedPassword;

    public Bcrypt(String password) {
        this.password = password;
        this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public String getHashedPassword() {
        return "{bcrypt}" + hashedPassword;
    }


}
