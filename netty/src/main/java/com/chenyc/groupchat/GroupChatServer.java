package com.chenyc.groupchat;

import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class GroupChatServer {
    //定义属性
    private Selector selector;

    private ServerSocketChannel listenChannel;

    private static final int PORT = 6667;

    public GroupChatServer() {

        try {
            //得到选择器
            selector = Selector.open();
            //得到serverSocketChannel
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        try {
            //循环处理
            while (true){
                int select = selector.select();
                if(select>0){
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        if(key.isAcceptable()){
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector,SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress()+"上线了");
                        }
                        if(key.isReadable()){
                            readData(key);
                        }
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待中");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    //读取客户端消息
    private void readData(SelectionKey key){
        //定义个SocketChannel
        SocketChannel channel = null;
        try {
            //取到关联的channel
            channel = (SocketChannel) key.channel();
            //创建buffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int count = channel.read(byteBuffer);
            //根据count值判断是否读取到数据
            if(count>0){
                //把缓冲区的数据转成字符串
                String msg = new String(byteBuffer.array());
                //输出该消息
                System.out.println("from 客户端：" + msg);

                //向其他的客户端转发消息（去掉自己），专门写一个方法处理
                sendInfoToOtherClients(msg,channel);
            }

        } catch (Exception e) {
            try {
                System.out.println(channel.getRemoteAddress()+"离线了");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void sendInfoToOtherClients(String msg, SocketChannel channel) throws IOException {
        System.out.println("服务器转发消息中。。。");
        //遍历 所有注册到selector上的SocketChannel，并排除self
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            //通过key取出对应的SocketChannel
            Channel targetChannel =key.channel();

            //排除自己
            if (targetChannel instanceof SocketChannel) {
                if(targetChannel != channel){
                    //转型
                    SocketChannel dest = (SocketChannel) targetChannel;
                    //将msg，存储到buffer
                    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                    //将buffer的数据写入通道
                    dest.write(buffer);
                }

            }
        }
    }

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
