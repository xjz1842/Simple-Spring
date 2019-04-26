package com.simple.spring.bean;

public interface BeanFactory {

    Object getBean(String beanName);


    boolean removeBean(String beanName);


    boolean destroy(String beanName);

}

