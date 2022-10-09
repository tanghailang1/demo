package com.example.demo.test;


import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class TestJar {

    public static void main(String[] args) throws Exception {
        //路径要加file
        URL url = new URL("file:/Users/sean/Desktop/Test.jar");
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
        Class<?> clazz = urlClassLoader.loadClass("Test");
        System.out.println(clazz.getName());
        Method[] methods = clazz.getMethods();
//        for (int i = 0; i < methods.length; i++) {
//            System.out.println(methods[i].getName());
//        }
//        System.load("/Users/sean/my/demo/src/resources/banner.txt");
//        System.load("/Users/sean/my/demo/src/resources/Test.cpython-36m-x86_64-linux-gnu.so");

        //如果入参则需要在后面加入参
        Method method = clazz.getMethod("loadLibrary");
        String s= (String) method.invoke(clazz.newInstance());
        System.out.println(s);
    }

//    public native static void initModule();
//    public native static void uninitModule();
//    public native static String testFunction(String param);
//    public synchronized static void loadLibrary() {
//        initModule();
//        testFunction("fdstgfdr hk");
//        //uninitModule();
//    }

}
