package com.example.demo.test;

public class Test {
    public native static void initModule();
    public native static void uninitModule();
    public native static String testFunction(String param);
    public synchronized static void loadLibrary() {
        //System.load("/Users/sean/Desktop/Test.cpython-36m-x86_64-linux-gnu.so");
        System.load("/Users/sean/my/demo/src/resources/Test.cpython-36m-x86_64-linux-gnu.so");
        System.out.println("fd");
        initModule();
        testFunction("fdstgfdr hk");
        uninitModule();
    }
    public static void main(String args[]) {
        loadLibrary();
    }

}