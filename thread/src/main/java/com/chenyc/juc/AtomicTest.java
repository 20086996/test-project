package com.chenyc.juc;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 类的小工具包，支持在单个变量上解除锁的线程安全编程。事实上，此包中的类可 将 volatile 值、字段和数组元素的概念扩展到那些也提供原子条件更新操作的类。
 *
 * 类 AtomicBoolean、AtomicInteger、AtomicLong 和 AtomicReference 的实例各自提供对 相应类型单个变量的访问和更新。每个类也为该类型提供适当的实用工具方法。
 *
 * AtomicIntegerArray、AtomicLongArray 和 AtomicReferenceArray 类进一步扩展了原子操 作，对这些类型的数组提供了支持。这些类在为其数组元素提供 volatile 访问语义方 面也引人注目，这对于普通数组来说是不受支持的。
 *
 * 核心方法：boolean compareAndSet(expectedValue, updateValue)
 *
 * java.util.concurrent.atomic 包下提供了一些原子操作的常用类: AtomicBoolean 、AtomicInteger 、AtomicLong 、 AtomicReference AtomicIntegerArray 、AtomicLongArray ,AtomicMarkableReference ,AtomicReferenceArray ,AtomicStampedReference
 * 具体的一些方法，可查看API文档
 *
 * i++ 原子性问题，先读取到i 然后再 ++ ,操作被分开了，有同步安全问题
 *
 * 原子变量 jdk1.5后 java.util.concurrent.atomic包下提供了常用的原子变量
 *
 * 1.volatile 保证内存可见性
 *
 * 2.CAS算法 保证数据的原子性
 *
 * CAS算法是硬件对于并发操作共享数据的支持
 *
 * 包含了三个操作数：
 *
 * 内存值 V
 *
 * 预估值 A
 *
 * 更新值 B
 *
 * 当且仅当 V == A时，V = B，否则，将不做任何操作
 *
 * @author chenyc
 * @create 2020-08-20 14:15
 */
public class AtomicTest {
    @Test
    public void test1(){
        Atomic atomic = new Atomic();
        for (int i=0;i<10;i++){
            new Thread(atomic).start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}


class Atomic implements Runnable{
    /**并发时volatile同样具有线程安全问题，不具备原子性*/
    private volatile int num = 0;
    /**并发时AtomicInteger*/
    private AtomicInteger atomicInteger =new AtomicInteger();

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+":"+atomicInteger.getAndIncrement());
    }
}