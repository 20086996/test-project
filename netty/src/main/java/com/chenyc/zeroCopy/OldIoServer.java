package com.chenyc.zeroCopy;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author chenyc
 * @create 2020-08-26 15:31
 */
public class OldIoServer {

    @Test
    public void test01(){

    }

    public static void main(String[] args) throws Exception {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7002);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(inetSocketAddress);

        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int leng =0;
        while ((leng=inputStream.read(bytes))!=-1){
            leng+=leng;
        }
        System.out.println(leng);
        socket.close();
    }
}
