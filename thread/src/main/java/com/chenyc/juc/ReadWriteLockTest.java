package com.chenyc.juc;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 1. ReadWriteLock : 读写锁
 *
 * 写写/读写 需要“互斥”
 * 读读 不需要互斥
 *
 */
public class ReadWriteLockTest {
    public static void main(String[] args) {
        ReadWriteLockDemo readWriteLockDemo = new ReadWriteLockDemo();
        new Thread(()->{
            readWriteLockDemo.set((int)(Math.random()*101));
        },"write").start();

        for (int i=0;i<100;i++){
            new Thread(()->{
                readWriteLockDemo.get();
            }).start();
        }
    }

}
class ReadWriteLockDemo {
    private int num =0;
    private ReadWriteLock lock = new ReentrantReadWriteLock();


    public void get(){
        lock.readLock().lock();

        try {
            System.out.println(Thread.currentThread().getName()+":"+num);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void set(int number){
        lock.writeLock().lock();

        try {
            System.out.println(Thread.currentThread().getName()+":"+num);
            this.num =number;

        } finally {
            lock.writeLock().unlock();
        }
    }
}