package com.chenyc.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //向管道加入处理器


        //得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        //加入一个netty提供的httpServerCode
        //1、httpServerCode 是netty提供的处理http请求的编解码器
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        //2、增加一个自定义handler
        pipeline.addLast("MyHttpServerHandler",new HttpServerHandler());
    }
}
