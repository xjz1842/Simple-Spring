package com.simple.spring.domain.impl;

import com.simple.spring.annotation.After;
import com.simple.spring.annotation.Before;
import com.simple.spring.annotation.Component;
import com.simple.spring.domain.UserService;

@Component
public class UserServiceImpl implements UserService {

    @Before
    @After
    public boolean login(String userName, String password) {
        System.out.println("login:"+userName+":"+password);
        return false;
    }
}
