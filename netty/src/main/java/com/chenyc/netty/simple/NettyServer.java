package com.chenyc.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.Test;

public class NettyServer {
    public static void main(String[] args) throws Exception {
        //创建BossGroup 和 WorkerGroup
        //1、创建两个线程组，bossGroup 和 workerGroup
        //2、bossGroup 只是处理连接请求，真正的和客户端业务处理，会交给 workerGroup 完成
        //3、两个都是无限循环
        //4、bossGroup 和 workerGroup 含有的子线程（NioEventLoop）个数为实际 cpu 核数 * 2
        //private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
        //16个nioEventLoop
        //
        //1、netty抽象出两组线程池，BossGroup专门负责接收客户端连接，WorkerGroup专门负责接收网络读写操作
        //2、bossGroup 和 workerGroup 含有的子线程（NioEventLoop）个数为实际 cpu 核数 * 2
        //3、NioEventLoop表示一个不断循环执行处理任务的线程，每个NioEventLoop都有一个selector，用来监听绑定在其上的SocketChannel网络通道
        //4、NioEventLoop内部采用串行化设计，从消息的读取-》解码-》处理-》编码-》编码-》发送，始终由IO线程NioEventLoop负责
            //每个NioEventLoop下含有多个NioEventLoop
            //每个NioEventLoop的Selector上含有一个Selector,一个taskQueue
            //每个NioEventLoop的Selector上可以注册监听多个NioChannel
            //每个NioChannel只会绑定在唯一的NioEventLoop上
            //m每个NioChannel都有绑定有一个自己的channelPipeline
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
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("........服务器 is ready...");
            //绑定一个端口并且同步，生成了一个ChannelFuture 对象
            //启动服务器（并绑定端口）
            ChannelFuture cf = bootstrap.bind(6668).sync();

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

    @Test
    public void test02(){

    }
}
