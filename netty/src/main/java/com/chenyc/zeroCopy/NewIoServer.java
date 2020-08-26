package com.chenyc.zeroCopy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author chenyc
 * @create 2020-08-26 12:58
 */
public class NewIoServer {
    public static void main(String[] args) throws Exception {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7001);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(inetSocketAddress);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = serverSocketChannel.accept();
        int leng = 0;
        while ((leng=socketChannel.read(byteBuffer))!=-1){
            byteBuffer.clear();
        }
        System.out.println(leng);
        socketChannel.close();
    }
}
