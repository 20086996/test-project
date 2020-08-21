package com.chenyc.juc;

import org.junit.Test;
import sun.applet.Main;

/**
 * Java 提供了一种稍弱的同步机制，即 volatile 变量，用来确保将变量的更新操作通知到其他线程。可以将 volatile 看做一个轻量级的锁，但是又与锁有些不同：
 *
 * 对于多线程，不是一种互斥关系
 * 不能保证变量状态的 “原子性操作”
 *
 *
 * @author chenyc
 * @create 2020-08-20 12:44
 */
public class VolatileTest {
    @Test
    public void test1(){
        ThreadDemo threadDemo = new ThreadDemo();
        new Thread(threadDemo).start();

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(threadDemo.isFlag());
        System.out.println(this.getClass());
        while (true){
            if(threadDemo.isFlag()){
                System.out.println("--------------------");
                break;
            }
        }
        /**flag=true*/
    }

    @Test
    public void test2(){
        ThreadDemo threadDemo = new ThreadDemo();
        new Thread(threadDemo).start();
        while (true){
            // 效率低
            synchronized (this) {
                if (threadDemo.isFlag()) {
                    System.out.println("--------------------");
                    break;
                }
            }
        }
        /**
         * --------------------
         * flag=true
         * */
    }

    @Test
    public void test3(){
        ThreadDemo threadDemo = new ThreadDemo();
        new Thread(threadDemo).start();
        while (true){
            if (threadDemo.isFlag()) {
                System.out.println("--------------------");
                break;
            }
        }
        /**
         * --------------------
         * flag=true
         * */
    }
}

class  ThreadDemo implements  Runnable{
    //test1和test2没用Volatile关键字
    //private   boolean flag = false;

    //test3用Volatile关键字
    private  volatile   boolean flag = false;

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flag = true;
        System.out.println("flag="+flag);
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}