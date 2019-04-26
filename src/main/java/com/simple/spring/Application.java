package com.simple.spring;


import com.simple.spring.annotation.ComponentScan;
import com.simple.spring.bean.DefaultListableBeanFactory;
import com.simple.spring.domain.UserService;


@ComponentScan("com.simple.spring")
public class Application {

    public static void main(String[] args) {
        ComponentScan componentScan = Application.class.getAnnotation(ComponentScan.class);
        String classPath = componentScan.value();
        DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory(classPath);
        UserService userService = (UserService) defaultListableBeanFactory.getBean("UserServiceImpl");
        userService.login("zhanxuejun","123456");

    }
}
