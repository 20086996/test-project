package com.chenyc.nio;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel03 {

    @Test
    public void test02() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("file01.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("file02.txt");

        byte[] bytes = new byte[1024];
        int len=0;
        while ((len=fileInputStream.read(bytes))!=-1){
            System.out.println(new String(bytes, 0, len));
            fileOutputStream.write(bytes,0,len);
        }

        fileInputStream.close();
        fileOutputStream.close();
    }

    @Test
    public void test01() throws Exception{
        //创建文件输入流
        FileInputStream fileInputStream = new FileInputStream("file01.txt");
        //创建文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream("file03.txt");

        //获取通道
        FileChannel inChannel = fileInputStream.getChannel();
        FileChannel outcChannel = fileOutputStream.getChannel();

        //创建byteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        //将通道的数据读入byteBuffer
        int len;
        while ((len=inChannel.read(byteBuffer))!=-1){
            byteBuffer.flip();
            outcChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        fileInputStream.close();
        fileOutputStream.close();
    }
}
