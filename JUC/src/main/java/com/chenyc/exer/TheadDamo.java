package com.chenyc.exer;

/**
 * 练习：创建两个分线程，其中一个线程遍历100以内的偶数，另一个线程遍历100以内的奇数
 * @author chenyc
 * @create 2020-08-18 11:11
 */
public class TheadDamo {
    public static void main(String[] args) {
//        MyThead1 myThead1 = new MyThead1();
//        MyThead2 myThead2 = new MyThead2();
//        myThead1.start();
//        myThead2.start();

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    if (i % 2 == 0) {
//                        System.out.println(Thread.currentThread().getName() + "------" + i);
//                    }
//                }
//            }
//        };
//        thread.start();
//
//        Thread thread1 = new Thread() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    if (i % 2 != 0) {
//                        System.out.println(Thread.currentThread().getName() + "------" + i);
//                    }
//                }
//            }
//        };
//        thread1.start();

        Runnable runnable = () -> {
            for (int i = 0; i < 10; i++) {
                if (i % 2 == 0) {
                    System.out.println(Thread.currentThread().getName() + "------" + i);
                }
            }
        };
        Thread thread2 = new Thread(runnable);
        thread2.start();

        Runnable runnable1 = () -> {
            for (int i = 0; i < 10; i++) {
                if (i % 2 != 0) {
                    System.out.println(Thread.currentThread().getName() + "------" + i);
                }
            }
        };
        Thread thread3 = new Thread(runnable1);
        thread3.start();

    }
}


class MyThead1 extends Thread{
    @Override
    public void run() {
        for (int i=0;i<100;i++){
            if(i%2==0){
                System.out.println(Thread.currentThread().getName()+"------"+i);
            }
        }
    }
}

class MyThead2 extends Thread{
    @Override
    public void run() {
        for (int i=0;i<100;i++){
            if(i%2!=0){
                System.out.println(Thread.currentThread().getName()+"------"+i);
            }
        }
    }
}