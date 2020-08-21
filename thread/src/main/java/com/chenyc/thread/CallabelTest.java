package com.chenyc.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 创建线程的方式三：实现Callable接口。 --- JDK 5.0新增
 *
 *
 * 如何理解实现Callable接口的方式创建多线程比实现Runnable接口创建多线程方式强大？
 * 1. call()可以有返回值的。
 * 2. call()可以抛出异常，被外面的操作捕获，获取异常的信息
 * 3. Callable是支持泛型的
 *
 * @author chenyc
 * @create 2020-08-19 10:42
 */
public class CallabelTest {
    public static void main(String[] args) {
        NumThread numThread = new NumThread();
        FutureTask futureTask = new FutureTask<>(numThread);

        Thread thread = new Thread(futureTask);
        thread.start();

        try {
            Object o = futureTask.get();
            System.out.println(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("=======================");
    }
}


class  NumThread implements Callable{
    private int sum=0;

    @Override
    public Object call() throws Exception {
        for (int i =0;i<10000;i++){
            if(i%2==0){
                sum+=i;
            }
        }
        return sum;
    }
}