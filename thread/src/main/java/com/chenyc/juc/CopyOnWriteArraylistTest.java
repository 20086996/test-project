package com.chenyc.juc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chenyc
 * @create 2020-08-20 15:25
 */
public class CopyOnWriteArraylistTest {
    public static void main(String[] args) {
        HelloThresd helloThresd=new HelloThresd();
        for(int i=0;i<20;i++){
            new Thread(helloThresd).start();
        }
    }

}

class HelloThresd implements Runnable{

    //报错并发修改异常
    //private static List<String> list = Collections.synchronizedList(new ArrayList<String>());
    //每次添加时候都复制一下新的，不适合大量添加修改，适合都读多写少
    private static CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
    static {
        list.add("aa");
        list.add("bb");
        list.add("cc");
    }

    @Override
    public void run() {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            System.out.println(next);
            list.add("dd");
        }
    }
}