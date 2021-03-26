package demo;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyc
 * @create 2020-10-14 10:53
 *
 * -Xmx20m -XX:+PrintGCDetails -verbose:gc
 */
public class Demo3 {
    private static final int _4MB=4*1024*1024;

    public static void main(String[] args) {
        List<WeakReference<byte[]>> list= new ArrayList<>();//弱引用
        for (int i = 0; i < 5; i++) {
            WeakReference<byte[]> ref=new WeakReference<>(new byte[_4MB]);
            list.add(ref);
            System.out.println("第"+(i+1)+"次循环");
            for ( WeakReference<byte[]> w:list) {
                System.out.println(w.get()+"");
            }
            System.out.println();
        }
        System.out.println("循环结束："+list.size());
    }
}
