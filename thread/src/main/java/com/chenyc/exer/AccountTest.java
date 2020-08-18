package com.chenyc.exer;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 银行有一个账户。
 有两个储户分别向同一个账户存3000元，每次存1000，存3次。每次存完打印账户余额。

 分析：
 1.是否是多线程问题？ 是，两个储户线程
 2.是否有共享数据？ 有，账户（或账户余额）
 3.是否有线程安全问题？有
 4.需要考虑如何解决线程安全问题？同步机制：有三种方式。

 * @author chenyc
 * @create 2020-08-18 20:23
 */
public class AccountTest {
    public static void main(String[] args) {
        Account account = new Account(0);
        Customer customer1 = new Customer(account);
        Customer customer2 = new Customer(account);

        customer1.setName("甲");
        customer2.setName("乙");

        customer1.start();
        customer2.start();;
    }
}

class Account {
    private double balance;

    public Account(double balance) {
        this.balance = balance;
    }

    private ReentrantLock lock = new ReentrantLock();

    /**方式一：public synchronized void deposit(double amt){*/
    public  void deposit(double amt){
        if(amt>0){
            /**方式二：this代表Account，由于唯一可以正确锁住*/
            //synchronized (this) {
            /**方式三：ReentrantLock*/
            try {
                lock.lock();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                balance+=amt;
                System.out.println(Thread.currentThread().getName()+"存款成功，余额为："+balance);
            } finally {
                lock.unlock();
            }
            //}
        }
    }
}

class Customer extends Thread{
    private Account account;

    public Customer(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        for (int i=0;i<3;i++){
            account.deposit(1000);
        }
    }
}