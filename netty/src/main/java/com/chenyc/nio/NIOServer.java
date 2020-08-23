package com.chenyc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception {
        //创建ServerSocketChannel -> ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //得到一个Selector对象
        Selector selector = Selector.open();
        //绑定一个端口6666
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);

        //把 serverSocketChannel 注册到 selector ，关心事件为：OP_ACCEPT，有新的客户端连接
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true){
            //等待1秒，如果没有事件发生，就返回
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待了1秒，无连接");
                continue;
            }

            //如果返回的 > 0,表示已经获取到关注的事件
            // 就获取到相关的 selectionKey 集合，反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //遍历 Set<SelectionKey>，使用迭代器遍历
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                //获取到SelectionKey
                SelectionKey key = iterator.next();
                //根据 key 对应的通道发生的事件，做相应的处理
                if(key.isAcceptable()){//如果是 OP_ACCEPT，有新的客户端连接
                    //该客户端生成一个 SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功，生成了一个SocketChannel：" + socketChannel.hashCode());

                    //将SocketChannel设置为非阻塞
                    socketChannel.configureBlocking(false);

                    //将socketChannel注册到selector，关注事件为 OP_READ，同时给SocketChannel关联一个Buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if(key.isReadable()){
                    //通过key反向获取channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取该channel关联的buffer
                    ByteBuffer byteBuffer= (ByteBuffer) key.attachment();
                    //将当前channel数据读到buffer中
                    channel.read(byteBuffer);
                    System.out.println("from 客户端：" + new String(byteBuffer.array()));
                }

                //手动从集合中移除当前的 selectionKey，防止重复操作
                iterator.remove();
            }

        }

    }
}
