package com.chenyc.netty.codec2;

import com.chenyc.netty.codec.StudentPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class NettyServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {
    /**
     *读取客户端发送过来的消息
     * @param ctx 上下文对象，含有 管道pipeline，通道channel，地址
     * @param msg 就是客户端发送的数据，默认Object
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MyDataInfo.MyMessage myMessage) throws Exception {
        //根据dataType显示不同信息
        MyDataInfo.MyMessage.DateType dateType = myMessage.getDateType();
        if(dateType==MyDataInfo.MyMessage.DateType.StudentType){
            MyDataInfo.Student student = myMessage.getStudent();
            System.out.println("学生id："+student.getId()+":"+student.getName());
        }
        else if(dateType==MyDataInfo.MyMessage.DateType.WorkerType){
            MyDataInfo.Worker worker = myMessage.getWorker();
            System.out.println("工人年龄："+worker.getAge()+":"+worker.getName());
        }
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
