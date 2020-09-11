package com.chenyc.netty.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyWebsocketServer {

    public static void main(String[] args) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程来进行设置，配置
            //设置两个线程组
            bootstrap.group(bossGroup,workerGroup)
                    ///使用 NioServerSocketChannel 作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG,128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //为accept channel的pipeline预添加的handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //给 pipeline 添加处理器，每当有连接accept时，就会运行到此处。
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("客戶SocketChannel hashcode="+ch.hashCode());
                            //可以使用一个集合管理SocketChannel，再推送消息时可以将业务加入到各个channel对应的NIOEventLoop的taskQueue或者scheduleTaskQueue中
                            //基于http协议，使用http的编码和解码器
                            ch.pipeline().addLast(new HttpServerCodec());
                            //以块的方式写，添加
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            /*
                            * http数据在传输中是分段的，HttpObjectAggregator就是将多个段请求聚合
                            * 这就是为什么当浏览器发送大量请求的时候，就会发出多次http请求
                            * */
                            ch.pipeline().addLast(new HttpObjectAggregator(8192));
                            /*
                            * 对于websocker数据是以帧形式来传递
                            * webSocketFrame里有6个子类
                            * 浏览器请求时：ws://localhost:8080/xxx
                            * WebSocketServerProtocolHandler将http协议升级为ws协议，保持长连接
                            * 是通过一个状态码将http协议升级
                            * */
                            ch.pipeline().addLast(new WebSocketServerProtocolHandler("/hello"));
                            ch.pipeline().addLast(new NettyWebsocketServerHandler());
                        }
                    });
            System.out.println("........服务器 is ready...");
            //绑定一个端口并且同步，生成了一个ChannelFuture 对象
            //启动服务器（并绑定端口）
            ChannelFuture cf = bootstrap.bind(8080).sync();

            //给cf注册监听器，监控我们关心的数据
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(cf.isSuccess()){
                        System.out.println("监听端口6668 成功");
                    }else {
                        System.out.println("监听端口6668 失败");
                    }
                }
            });

            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
