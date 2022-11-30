package com.artem.junit.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.artem.junit.dao.UserDao;
import com.artem.junit.dto.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserService {

    private final List<User> users = new ArrayList<>();
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean delete(Integer userId) {
        var userIdLocal = 25;
        return userDao.delete(userId);
    }

    public List<User> getAll() {
        return users;
    }

    public void add(User... user) {
        this.users.addAll(Arrays.asList(user));
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password is null");
        }

        return users.stream()
            .filter(user -> user.getUsername().equals(username))
            .filter(user -> user.getPassword().equals(password))
            .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream()
            .collect(toMap(User::getId, identity()));
    }
}
