package com.chenyc.nio;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel01 {

    @Test
    public void test02() throws Exception {
        String str= "hello";
        FileOutputStream fileOutputStream = new FileOutputStream("file01.txt");
        fileOutputStream.write(str.getBytes());

    }

    @Test
    public void test01() throws Exception{
        String str ="hello";
        //创建文件的输入流
        FileOutputStream fileOutputStream = new FileOutputStream("file01.txt");
        //通过fileInputStream 获取对应的FileChannel -> 实际类型 FileChannelImpl
        FileChannel channel = fileOutputStream.getChannel();
        //创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //将str放入byteBuffer
        byteBuffer.put(str.getBytes());

        //对bytebuffer进行翻转，进行flip操作
        byteBuffer.flip();

        //将byteBuffer数据写入fileChannel
        channel.write(byteBuffer);

        fileOutputStream.close();
    }
}
