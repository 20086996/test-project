package com.chenyc.juc;

/**
 * @author chenyc
 * @create 2020-08-20 14:38
 */
public class CasTest {
    public static void main(String[] args) {
        CompareAndGet compareAndGet = new CompareAndGet();
        for(int i =0;i<20;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /**1、第一次获取内存中的值*/
                    int expeceteValue = compareAndGet.get();
                    boolean b = compareAndGet.compareAndSet(expeceteValue, (int) (Math.random() * 101));
                    System.out.println(b);
                }
            }).start();
        }

    }
}

class CompareAndGet{
    private int value;

    //获取内存值
    public synchronized int get(){
        return value;
    }

    //比较
    public synchronized  int compareAndSwap(int expeceteValue,int newValue){
        /**2、第二次获取内存中的值，比较和第一次是否被修改*/
        int oldValue = value;
        if(oldValue==expeceteValue){
            this.value= newValue;
        }
        return oldValue;
    }

    //设置
    public synchronized boolean compareAndSet(int expeceteValue,int newValue){
        return expeceteValue==compareAndSwap(expeceteValue, newValue);
    }
}