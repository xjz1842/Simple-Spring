package com.simple.spring.bean;

import com.simple.spring.annotation.Aspect;
import com.simple.spring.annotation.Component;
import com.simple.spring.aop.JdkDynamicAopProxy;
import com.simple.spring.util.ClassUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory implements BeanFactory{

    private Map<String,Object> beanMap = new ConcurrentHashMap<String, Object>();

    private List<Class<?>> clasList = null;


    public DefaultListableBeanFactory(String classPath){
        clasList = ClassUtil.getClassList(classPath,true);
        instanitialBean();
    }

    public void instanitialBean(){
        for(Class<?> clazz : clasList){
            if(clazz.isAnnotationPresent(Component.class)) {
                beanMap.put(clazz.getSimpleName(), ClassUtil.instance(clazz));
            }
        }

        for(Class<?> clazz : clasList){
            if (clazz.isAnnotationPresent(Aspect.class)) {
                Aspect annotation = clazz.getAnnotation(Aspect.class);
                Object target = beanMap.get(annotation.value().getSimpleName());
                if(target != null){
                    Object proxy = new JdkDynamicAopProxy(target,clazz).getProxy();
                    beanMap.put(target.getClass().getSimpleName(),proxy);
                }
            }
        }
    }

    public Object getBean(String beanName) {
        return beanMap.get(beanName);
    }

    public boolean removeBean(String beanName) {
        return false;
    }

    public boolean destroy(String beanName) {
        return false;
    }
}
