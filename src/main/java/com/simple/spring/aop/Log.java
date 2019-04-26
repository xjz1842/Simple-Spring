package com.simple.spring.aop;

import com.simple.spring.annotation.After;
import com.simple.spring.annotation.Aspect;
import com.simple.spring.annotation.Before;
import com.simple.spring.domain.impl.UserServiceImpl;


@Aspect(value = UserServiceImpl.class)
public class Log {

    @Before
    public void before(){
        System.out.println("before");
    }

    @After
    public void after(){
        System.out.println("after");
    }
}
