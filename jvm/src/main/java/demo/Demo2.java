package demo;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyc
 * @create 2020-10-14 10:53
 *
 * -Xmx20m -XX:+PrintGCDetails -verbose:gc
 */
public class Demo2 {
    private static final int _4MB=4*1024*1024;

    public static void main(String[] args) {
        //强引用
        ArrayList<byte[]> list = new ArrayList<>();
        for (int i=0;i<5;i++){
            list.add(new byte[_4MB]);
            System.out.println(list.size());
        }
//        softQueue();
    }

    public static void soft(){
        //软引用
        List<SoftReference<byte[]>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SoftReference<byte[]> ref=new SoftReference<>(new byte[_4MB]);
            System.out.println(ref.get());//正常显示
            list.add(ref);
            System.out.println(list.size());

        }
        //内存不够进行了垃圾回收，软引用垃圾回收后内容扔不足就会把软引用扔掉
        System.out.println("循环结束"+list.size());
        for (SoftReference<byte[]> ref:list) {
            //前4个都变为null
            System.out.println(ref.get());
        }
    }

    public static void softQueue(){
        ReferenceQueue<byte[]> queue=new  ReferenceQueue<>();//创建引用队列

        //软引用
        List<SoftReference<byte[]>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            //当软引用所关联的的byte[]回收时，软引用自身就会被加入到queue中去。遍历时，就先到queue中查找
            SoftReference<byte[]> ref=new SoftReference<>(new byte[_4MB],queue);
            System.out.println(ref.get());//正常显示
            list.add(ref);
            System.out.println(list.size());

        }
        //每次取一个
        Reference<?extends byte[]> poll=queue.poll();
        while(poll!=null){
            list.remove(poll);
            //取下个
            poll=queue.poll();
        }

        //内存不够进行了垃圾回收，软引用垃圾回收后内容扔不足就会把软引用扔掉
        System.out.println("循环结束"+list.size());
        for (SoftReference<byte[]> ref:list) {
            //前4个都变为null
            System.out.println(ref.get());
        }
    }
}
