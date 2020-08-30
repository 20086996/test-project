package com.chenyc.netty.group_chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NettyGroupServerHandler extends SimpleChannelInboundHandler<String>{
    //使用一个hashmap管理
    public static Map<String,Channel> channelMap = new HashMap<>();

    //定义一个channel组，管理所有的chanel,GlobalEventExecutor全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //链接建立，第一个被执行，将当前channel加入到channelGroup中
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息，推送给其他在线的客户端,将group中的所有channel遍历，并发送，无需自己遍历
        channelGroup.writeAndFlush("客户端"+channel.remoteAddress()+"加入聊天"+simpleDateFormat.format(new Date())+"\n");
        channelGroup.add(channel);

        channelMap.put("userId",channel);
    }

    //表示channel处于活动状态，提示xx上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"上线了");
    }

    //表示channel处于非活动状态，提示xx下线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"离线了");
    }

    //表示断开连接,将Xx离开的信息推送给当前在线的客户,会把当前channel从channelGroup中移除
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("客户端"+channel.remoteAddress()+"离开了\n");
    }

    //读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取当前channel
        Channel channel = ctx.channel();
        //遍历channelGroup，根据不同情况，回送不同的消息
        channelGroup.forEach(ch->{
            if(channel!=ch){
                ch.writeAndFlush("【客户端】"+channel.remoteAddress()+"说："+msg+"\n");
            }else {
                ch.writeAndFlush("【自己说】"+channel.remoteAddress()+"说："+msg+"\n");
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
