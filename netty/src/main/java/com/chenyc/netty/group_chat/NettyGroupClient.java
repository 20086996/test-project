package com.chenyc.netty.group_chat;

import com.chenyc.netty.simple.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class NettyGroupClient {

    private final  String host;
    private final int port;


    public NettyGroupClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run(){
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder",new StringDecoder());
                            pipeline.addLast("encoder",new StringEncoder());
                            pipeline.addLast(new NettyGroupClientHandler());
                        }
                    });

            ChannelFuture sync = bootstrap.connect(host, port).sync();
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()){
                        System.out.println("连接成功");
                    }
                }
            });

            Channel channel = sync.channel();
            System.out.println("==========="+channel.remoteAddress()+"========");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                channel.writeAndFlush(msg+"\r\n");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }

    }


    public static void main(String[] args) {
        NettyGroupClient localhost = new NettyGroupClient("localhost", 8080);
        localhost.run();
    }
}
