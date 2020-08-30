package com.chenyc.netty.heart_beat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyHeartServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //加入一个netty提供的IdleStateHandler
                            /*

                            handlerRemoved：无法百分百准确感知客户端断掉，所以要隔断时间发一下心跳检测包，准确感知这个连接是否有效
                            * long readerIdleTime, 表示多少时间内服务端没有接到客户端的读事件，就会发送一个心跳检测包检测是都是否连接
                            * long writerIdleTime,表示多少时间服务端没有写数据给客户端，就会发送一个心跳检测包检测是都是否连接
                            * long allIdleTime，表示
                            *
                            当IdleStateHandler被触发后，就会传递给管道的下一个handler去处理
                            通过调用下一个hanler的userEventTiggered，在该方法去处理
                            * */
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            pipeline.addLast(new NettyHeartServerHandler());
                        }
                    });

            ChannelFuture sync = serverBootstrap.bind(8080).sync();
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        System.out.println("监控端口成功");
                    }
                }
            });

            ChannelFuture sync1 = sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }
}
