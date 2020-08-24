package com.chenyc.nio;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel02 {

    @Test
    public void test02() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("d:\\file01.txt");

        byte[] bytes = new byte[1024];
        int len=0;
        while ((len=fileInputStream.read(bytes))!=-1){
            String s = new String(bytes, 0, len);
            System.out.println(s);
        }


    }

    @Test
    public void test01() throws Exception{
        //创建文件输入流
        File file = new File("d:\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        //获取通道
        FileChannel channel = fileInputStream.getChannel();

        //创建byteBuffer
        ByteBuffer allocate = ByteBuffer.allocate((int) file.length());

        //将通道的数据读入byteBuffer
        channel.read(allocate);

        //将字节转换成字符串
        byte[] array = allocate.array();
        String s = new String(array);
        System.out.println(s);

        fileInputStream.close();
    }
}
