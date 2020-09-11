package com.chenyc.netty.protobuf02;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Random;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当通道就绪就会触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int random = new Random().nextInt(3);
        MyDataInfo.MyMessage myMessage = null;
        if(random==0){
            myMessage = MyDataInfo.MyMessage.newBuilder().setDateType(MyDataInfo.MyMessage.DateType.StudentType)
                    .setStudent(MyDataInfo.Student.newBuilder().setId(1).setName("玉麒麟").build()).build();

        }else {
            myMessage = MyDataInfo.MyMessage.newBuilder().setDateType(MyDataInfo.MyMessage.DateType.WorkerType)
                    .setWorker(MyDataInfo.Worker.newBuilder().setAge(1).setName("小红").build()).build();
        }

        ctx.writeAndFlush(myMessage);

    }

    /**
     * 当通道有读取事件时，会触发
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("服务器回复的消息：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址：" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
