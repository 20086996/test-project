package com.chenyc.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ScatteringAndGAtheringTest {

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
        serverSocketChannel.socket().bind(inetSocketAddress);

        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端的连接（Telnet）
        SocketChannel socketChannel = serverSocketChannel.accept();
        //假定从客户端接受8个字节
        int msgLength = 8;
        //循环的读取
        while (true){
            int byteRead = 0;
            while (byteRead<msgLength){
                long read = socketChannel.read(byteBuffers);
                byteRead+=read;
                System.out.println("byteRead="+byteRead);
                //使用流打印，看看当前这个buffer的position和limit
                Arrays.stream(byteBuffers)
                        .map(buffer -> "position=" + buffer.position() + ", limit = " + buffer.limit())
                        .forEach(System.out::println);
            }

            Arrays.asList(byteBuffers).stream().forEach(ByteBuffer::flip);

            long byteWrite = 0 ;
            while (byteWrite<msgLength){
                long write = socketChannel.write(byteBuffers);
                byteWrite+=write;
            }

            Arrays.asList(byteBuffers).stream().forEach(ByteBuffer::clear);

            System.out.println("byteRead=" + byteRead + ", byteWrite=" + byteWrite
                    + ", msgLength=" + msgLength);

        }
    }
}
