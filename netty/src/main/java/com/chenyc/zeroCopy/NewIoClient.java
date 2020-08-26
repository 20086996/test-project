package com.chenyc.zeroCopy;

import org.junit.Test;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @author chenyc
 * @create 2020-08-26 13:08
 */
public class NewIoClient {


    @Test
    public void test01() throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 7001));

        FileInputStream fileInputStream = new FileInputStream("jdk_api_1.8_google.rar");

        long startTime = System.currentTimeMillis();

        //在Linux下一个 transferTo 方法就可以完成传输
        //在windows 下一次调用 transferTo 只能发送 8M，就需要分段传输文件
        //传输时的位置
        //transferTo 底层使用到零拷贝
        FileChannel channel = fileInputStream.getChannel();
        long transferCount = channel.transferTo(0, channel.size(), socketChannel);

        socketChannel.shutdownOutput();

        System.out.println("发送的总的字节数：" + transferCount + " 耗时：" + (System.currentTimeMillis() - startTime));
        socketChannel.close();
    }
}