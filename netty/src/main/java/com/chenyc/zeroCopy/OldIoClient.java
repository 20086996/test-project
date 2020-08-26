package com.chenyc.zeroCopy;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author chenyc
 * @create 2020-08-26 16:50
 */
public class OldIoClient {

    @Test
    public void test01() throws Exception {
        Long startTime = System.currentTimeMillis();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 7002);
        Socket socket = new Socket();
        socket.connect(inetSocketAddress);
        OutputStream outputStream = socket.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(new File("jdk_api_1.8_google.rar"));
        byte[] bytes = new byte[1024];
        int leng = 0;
        while ((leng=fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes);
        }
        Long endTime = System.currentTimeMillis();
        System.out.println("耗时:"+(endTime-startTime));
        outputStream.close();
    }
}
