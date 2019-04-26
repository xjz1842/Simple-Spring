package com.simple.spring.util;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil {

    /**
     * 给我一个包名，获取该包下的所有class文件
     *
     * @param packageName 包名 com.xxx.xxx
     * @param isRecursive 是否递归
     * @return 返回class文件的集合
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> getClassList(String packageName, boolean isRecursive) {

        //声明一个返回List
        List<Class<?>> classList = new CopyOnWriteArrayList<Class<?>>();
        //将对应的包名转换为路径
        try {
            //Enumeration枚举接口，当中有两个方法，一个是判断是否有下一个元素，还有一个是取到下一个元素
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace('.', '/'));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                //System.out.println(url);	//	file:/D:/SXTJava/annotation/bin/annotation
                if (url != null) {
                    //拿到文件的协议
                    String protocol = url.getProtocol();
                    //如果是file
                    if ("file".equals(protocol)) {
                        //取到文件的路径
                        String packagePath = url.getPath();// /D:/SXTJava/annotation/bin/annotation
                        addClass(classList, packagePath, packageName, isRecursive);
                    } else if ("jar".equals(protocol)) { //如果是jar包的情况:此情况没有测试
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile(); //取到jar包下的文件
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) { //遍历jarEnyries
                            JarEntry jarEntry = jarEntries.nextElement();//取到元素
                            String jarEntryName = jarEntry.getName(); //取到文件名
                            if (jarEntryName.endsWith(".class")) { //如果文件名以.class结尾，将对应的文件添加至集合中
                                String name = jarEntryName.substring(0, jarEntryName.lastIndexOf("."));
                                System.out.println(name);
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");//取到类名

                                if (isRecursive || className.substring(0, className.lastIndexOf(".")).equals(packageName)) {
                                    classList.add(Class.forName(className));
                                }
                            }

                        }

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classList;
    }

    /**
     * 将对应包名下的所有.class文件加入到classList集合中
     *
     * @param classList   存放classList文件的集合
     * @param packagePath 包路径
     * @param packageName 包名
     * @param isRecursive 是否递归
     * @throws ClassNotFoundException
     */
    private static void addClass(List<Class<?>> classList, String packagePath, String packageName,
                                 boolean isRecursive) throws ClassNotFoundException {
        //取到路径下所有的对应的文件，包括.class文件和目录
        File[] files = getClassFiles(packagePath);

        if (files != null) {  //如果files不为空的话，对它进行迭代

            for (File file : files) {
                //取到文件名
                String fileName = file.getName(); //Column.class
                if (file.isFile()) {//如果取到的是文件
                    //取到对应的类名,这里的类名是权限定名
                    String className = getClassName(packageName, fileName);
                    classList.add(Class.forName(className));
                } else {
                    if (isRecursive) {
                        ///D:/SXTJava/annotation/bin/annotation+包名（fileName:test）
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        //递归调用自己
                        addClass(classList, subPackagePath, subPackageName, isRecursive);
                    }
                }
            }

        }
    }

    /**
     * 获取子目录
     *
     * @param packagePath 包的路径
     * @param fileName    文件的路径
     * @return
     */
    private static String getSubPackagePath(String packagePath, String fileName) {
        String subPackagePath = fileName;
        if (packagePath != null && (!"".equals(packagePath))) {
            subPackagePath = packagePath + "/" + subPackagePath;
        }
        return subPackagePath;
    }

    /**
     * 获取子包名
     *
     * @param packageName
     * @param fileName
     * @return
     */
    private static String getSubPackageName(String packageName, String fileName) {
        String subPackageName = fileName;
        if (packageName != null && (!"".equals(packageName))) {
            subPackageName = packageName + "." + subPackageName;
        }
        return subPackageName;
    }


    /**
     * 根据注解筛选对应的class文件
     *
     * @param packageName     包名
     * @param annotationClass 注解类
     * @param isRecursive     是否递归
     * @return
     */
    public static List<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass, boolean isRecursive) {
        List<Class<?>> classList = new CopyOnWriteArrayList<Class<?>>();
        classList = getClassList(packageName, isRecursive);
        for (Class<?> clz : classList) {
            if (!clz.isAnnotationPresent(annotationClass)) {
                classList.remove(clz);
            }
        }
        return classList;
    }

    /**
     * 根据传入的包名和文件名返回对应类的全限定名
     *
     * @param packageName 包名
     * @param fileName    文件名 	类名.后缀名
     * @return 包名.类名
     */
    private static String getClassName(String packageName, String fileName) {
        String className = fileName.substring(0, fileName.indexOf("."));
        if (packageName != null && !"".equals(packageName)) {
            className = packageName + "." + className;
        }
        return className;
    }


    /**
     * 获取class文件
     *
     * @param packagePath
     * @return
     */
    private static File[] getClassFiles(String packagePath) {
        //FileFilter文件过滤器
        return new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                //如果是.class文件，或者是目录就返回true
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
    }

    public static Object instance(Class clazz) {
        try {
            return  clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
}

