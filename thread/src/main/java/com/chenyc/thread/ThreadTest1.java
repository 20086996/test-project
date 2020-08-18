package com.chenyc.thread;

/**
 * 创建多线程的方式二：实现Runnable接口
 * 1. 创建一个实现了Runnable接口的类
 * 2. 实现类去实现Runnable中的抽象方法：run()
 * 3. 创建实现类的对象
 * 4. 将此对象作为参数传递到Thread类的构造器中，创建Thread类的对象
 * 5. 通过Thread类的对象调用start()
 *
 *
 * 比较创建线程的两种方式。
 * 开发中：优先选择：实现Runnable接口的方式
 * 原因：1. 实现的方式没有类的单继承性的局限性
 *      2. 实现的方式更适合来处理多个线程有共享数据的情况。
 *
 * 联系：public class Thread implements Runnable
 * 相同点：两种方式都需要重写run(),将线程要执行的逻辑声明在run()中。
 *
 */
public class ThreadTest1 {
    public static void main(String[] args) {
        MyThread1 myThread1 = new MyThread1();
        Thread thread = new Thread(myThread1);
        thread.start();
    }

}

class MyThread1 implements Runnable{
    @Override
    public void run() {
        for (int i=0;i<100;i++){
            if(i%2==0){
                System.out.println(Thread.currentThread().getName()+i);
            }
        }

    }
}