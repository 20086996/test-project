package com.chenyc.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 创建线程的方式四：使用线程池
 *
 * 好处：
 * 1.提高响应速度（减少了创建新线程的时间）
 * 2.降低资源消耗（重复利用线程池中线程，不需要每次都创建）
 * 3.便于线程管理
 *      corePoolSize：核心池的大小
 *      maximumPoolSize：最大线程数
 *      keepAliveTime：线程没有任务时最多保持多长时间后会终止
 *
 *
 * 面试题：创建多线程有几种方式？四种！
 * @author chenyc
 * @create 2020-08-19 11:06
 */
public class ExecutorsTest {

    public static void main(String[] args) {
        /**执行指定的线程的操作。需要提供实现Runnable接口或Callable接口实现类的对象*/
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ThreadPoolExecutor executorService1 = (ThreadPoolExecutor) executorService;
        executorService1.setMaximumPoolSize(20);
        executorService1.setCorePoolSize(10);
        executorService1.setKeepAliveTime(1, TimeUnit.SECONDS);

        /**适合用于Runnable*/
        executorService.execute(new NumberThread());
        executorService.execute(new NumberThread1());
        /**适合用于Callable*/
//        executorService.submit()

        executorService.shutdown();
    }
}

class NumberThread implements  Runnable{

    @Override
    public void run() {
        for (int i =0;i<100;i++){
            if(i%2==0){
                System.out.println(Thread.currentThread().getName()+":"+i);
            }
        }
    }
}


class NumberThread1 implements  Runnable{

    @Override
    public void run() {
        for (int i =0;i<100;i++){
            if(i%2!=0){
                System.out.println(Thread.currentThread().getName()+":"+i);
            }
        }
    }
}