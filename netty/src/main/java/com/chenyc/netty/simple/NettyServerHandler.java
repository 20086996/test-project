package com.chenyc.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     *读取客户端发送过来的消息
     * @param ctx 上下文对象，含有 管道pipeline，通道channel，地址
     * @param msg 就是客户端发送的数据，默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //提交到taskQueue中异步执行
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("当前处理线程："+Thread.currentThread().getName());
                    Thread.sleep(1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("处理费时业务一了，才完成",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("当前处理线程："+Thread.currentThread().getName());
                    Thread.sleep(1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("处理费时业务二了，才完成",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //用户自定义定时任务-》该任务提交到scheduleTaskQueue
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("当前处理线程："+Thread.currentThread().getName());
                ctx.writeAndFlush(Unpooled.copiedBuffer("处理费时业务三了，才完成",CharsetUtil.UTF_8));
            }
        }, 5, TimeUnit.SECONDS);

        System.out.println("当前处理线程：" + Thread.currentThread().getName());
//        System.out.println("server ctx = " + ctx);


        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //它是 write + flush，将数据写入到缓存buffer，并将buffer中的数据flush进通道
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端",CharsetUtil.UTF_8));
    }

    //处理异常，一般是关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
