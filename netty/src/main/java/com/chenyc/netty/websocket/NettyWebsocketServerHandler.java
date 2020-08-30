package com.chenyc.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

public class NettyWebsocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        //TextWebSocketFrame表示文本帧
        System.out.println("服务器收到消息"+textWebSocketFrame.text());

        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("服务器时间"+ LocalDateTime.now()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //当web客户端链接后，就会触发此方法,asLongText唯一，asShortText不一定唯一
        System.out.println("handlerAdded被调用"+ctx.channel().id().asLongText());
        System.out.println("handlerAdded被调用"+ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved被调用"+ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生"+cause.getMessage());
        ctx.channel().close();
    }
}
