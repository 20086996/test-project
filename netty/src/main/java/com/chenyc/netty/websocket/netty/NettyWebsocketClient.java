package com.chenyc.netty.websocket.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author chenyc
 * @create 2020-09-09 10:12
 */
public class NettyWebsocketClient {

    public static void main(String[] args) {
        EventLoopGroup group = null;
        try {
            group = new NioEventLoopGroup();
            URI websocketURI = URI.create("wss://l10n-pro.huobiasia.club:443/-/s/pro/ws");//ws://localhost:8080/hello
            WebSocketClientHandshaker handShaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
            Bootstrap boot = new Bootstrap();
            boot.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .group(group)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IdleStateHandler(5, 5, 5, TimeUnit.SECONDS));
                            pipeline.addLast("http-codec", new HttpClientCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(1024 * 1024));
                            pipeline.addLast("hookedHandler", new NettyWebsocketClientHandler(handShaker));
                        }
                    });

            ChannelFuture cf = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync();
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(cf.isSuccess()){
                        System.out.println("连接成功");
                    }else {
                        System.out.println("连接失败");
                    }
                }
            });
            final Channel channel = cf.channel();
            ChannelFuture sync = handShaker.handshake(channel).sync();
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(cf.isSuccess()){
                        System.out.println("握手成功");
                    }else {
                        System.out.println("握手失败");
                    }
                }
            });

            //订阅数据
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sub", "market.ethbtc.kline.1min");
            jsonObject.put("id", "id1");
            channel.writeAndFlush(new TextWebSocketFrame(jsonObject.toString()));

            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("连接websocket异常:"+ e);
        }finally {
            group.shutdownGracefully();
        }
    }

}
