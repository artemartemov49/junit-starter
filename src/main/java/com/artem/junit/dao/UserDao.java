package com.artem.junit.dao;

import java.sql.DriverManager;
import lombok.SneakyThrows;

public class UserDao {

    @SneakyThrows
    public boolean delete(Integer userId) {
        try (var connection = DriverManager.getConnection("url", "username", "password")) {
            return true;
        }
    }
}
