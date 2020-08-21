package com.chenyc.juc;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenyc
 * @create 2020-08-20 15:48
 */
public class CountDownLatchTest {
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();

        final CountDownLatch countDownLatch = new CountDownLatch(5);
        LatchDemo latchDemo = new LatchDemo(countDownLatch);
        for (int i=0;i<5;i++){
            new Thread(latchDemo).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LocalDateTime now1 = LocalDateTime.now();
        System.out.println("耗费时间："+(Duration.between(now,now1).toMillis()));

    }
}

class LatchDemo implements Runnable{

    private CountDownLatch latch;

    public LatchDemo(final CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            for (int i=0;i<20;i++){
                if(i%2==0){
                    System.out.println(Thread.currentThread().getName()+i);
                }
            }
        } finally {
            latch.countDown();
        }
    }
}