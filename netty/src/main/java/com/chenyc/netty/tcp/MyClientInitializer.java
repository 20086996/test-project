package com.chenyc.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;


public class MyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        //1、特殊分隔符拆包
        ByteBuf byteBuf = Unpooled.copiedBuffer("$_".getBytes());
        pipeline.addLast(new DelimiterBasedFrameDecoder(1024,byteBuf));

        //2、定长截取信息拆包
//        pipeline.addLast(new FixedLengthFrameDecoder(5));
        pipeline.addLast(new ReadTimeoutHandler(5));
        pipeline.addLast(new MyClientHandler());
    }
}
