package com.artem.junit.dao;

import java.util.HashMap;
import java.util.Map;
import org.mockito.stubbing.Answer1;

public class UserDaoMock extends UserDao {

    private Map<Integer, Boolean> answers = new HashMap<>();
//    private Answer1<Integer, Boolean> answer1;

    @Override
    public boolean delete(Integer userId) {
//      invocation++;
        return answers.getOrDefault(userId, false);
    }
}
