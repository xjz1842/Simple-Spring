package com.simple.spring.ioc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private Map<String,Object> beanMap = new ConcurrentHashMap<String, Object>();



}
