package com.chenyc.lock;

/**
 * @author chenyc
 * @create 2020-08-18 16:28
 */
public class BankTest {
}

/**
 *
 */
class Bank{
    private Bank(){}

    private static Bank instance = null;

//    /**问题：两个线程同时进入，线程1*/
//    private static Bank getInstance(){
//        if(instance==null){
//            /**线程1到此处可能被挂起，线程2同时进入，创建了两个对象*/
//            instance = new Bank();
//        }
//        return instance;
//    }

    /**方式1，默认同步监视器，类本身Bank.class*/
//    private synchronized static Bank getInstance(){
//        if(instance==null){
//            instance = new Bank();
//        }
//        return instance;
//    }
    /**方式2，效率差，所有线程都在排队等待锁*/
//    private  static Bank getInstance(){
//        synchronized (Bank.class) {
//            if(instance==null){
//                instance = new Bank();
//            }
//        }
//        return instance;
//    }

    /**方式3，效率稍高，只有少数几个线程可能需要等待，剩余线程无需等待*/
    private  static Bank getInstance(){
        if (instance==null) {
            synchronized (Bank.class) {
                if(instance==null){
                    instance = new Bank();
                }
            }
        }
        return instance;
    }

}