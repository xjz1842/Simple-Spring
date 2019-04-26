package com.simple.spring.aop;

import com.simple.spring.annotation.After;
import com.simple.spring.annotation.Before;
import com.simple.spring.util.ClassUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkDynamicAopProxy implements InvocationHandler {

    private Object target;

    private InvocationHandler invocationHandler;

    private Class intercept;

    public JdkDynamicAopProxy(Object target,Class intercept) {
        this.target = target;
        this.intercept = intercept;
    }

    public  Object getProxy(){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),target.getClass().getInterfaces(),this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         Object object = ClassUtil.instance(intercept);
         Method[] methods = object.getClass().getDeclaredMethods();

         Method before = null;
         Method after = null;
         for(Method method1 : methods){
             if(method1.isAnnotationPresent(Before.class)){
                 before = method1;
             }else if(method1.isAnnotationPresent(After.class)){
                 after =  method1;
             }
         }
         before.invoke(object);
         Object result = method.invoke(target,args);
         after.invoke(object);
         return result;
    }
}
