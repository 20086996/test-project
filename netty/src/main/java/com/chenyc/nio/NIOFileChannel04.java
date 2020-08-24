package com.chenyc.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel04 {


    @Test
    public void test01() throws Exception{
        //创建文件输入流
        FileInputStream fileInputStream = new FileInputStream("file01.txt");
        //创建文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream("file04.txt");

        //获取通道
        FileChannel inChannel = fileInputStream.getChannel();
        FileChannel outcChannel = fileOutputStream.getChannel();

        outcChannel.transferFrom(inChannel,0,inChannel.size());

        fileInputStream.close();
        fileOutputStream.close();
    }
}
