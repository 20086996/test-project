package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyc
 * @create 2020-10-14 10:53
 */
public class Demo1 {
    public static void main(String[] args) throws IOException {
        List<Object> list = new ArrayList<>();
        list.add("a");
        list.add("b");

        System.out.println(1);
        System.in.read();

        list=null;
        System.out.println(2);
        System.in.read();
        System.out.println("end...");
    }
}
