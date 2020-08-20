package com.chenyc.bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {
    public static void main(String[] args) throws IOException {
        //1、创建一个线城市
        //2、如果有客户端链接，创建一个县城，与之通信

        ExecutorService newCachedThreadPoo = Executors.newCachedThreadPool();

        //创建serverSocket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动");
        while (true){
            //监听 等待客户端链接
            final Socket accept = serverSocket.accept();
            System.out.println("有客户端链接");

            //创建一个线程，与之通信
            newCachedThreadPoo.execute(new Runnable() {
                @Override
                public void run() {
                    handler(accept);
                }
            });

        }

    }

    //编写一个handler方法与客户端通信
    public static  void handler(Socket socket){
        try {
            byte[] bytes = new byte[1024];
            //通过socket获取一个输入流
            InputStream inputStream = socket.getInputStream();
            int len = 0;
            while ((len= inputStream.read(bytes))!=-1){
                System.out.println(new String(bytes,0,len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("关闭client链接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

