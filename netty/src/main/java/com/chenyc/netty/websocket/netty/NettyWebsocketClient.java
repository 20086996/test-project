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
            group = new NioEventLoopGroup(2);
            URI websocketURI = URI.create("wss://l10n-pro.huobiasia.club:80/-/s/pro/ws");//ws://localhost:8080/hello
//            String scheme = websocketURI.getScheme() == null ? "ws" : websocketURI.getScheme();
//            final String host = websocketURI.getHost() == null ? "127.0.0.1" : websocketURI.getHost();
//            final int port;
//            if (websocketURI.getPort() == -1) {
//                if ("ws".equalsIgnoreCase(scheme)) {
//                    port = 80;
//                } else if ("wss".equalsIgnoreCase(scheme)) {
//                    port = 443;
//                } else {
//                    port = -1;
//                }
//            } else {
//                port = websocketURI.getPort();
//            }
            WebSocketClientHandshaker handShaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
            Bootstrap boot = new Bootstrap();
            Channel channel = boot.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .group(group)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IdleStateHandler(5, 6, 5, TimeUnit.SECONDS));
                            pipeline.addLast("http-codec", new HttpClientCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(1024 * 1024));
                            pipeline.addLast("hookedHandler", new NettyWebsocketClientHandler(handShaker));
                        }
                    }).connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();

//            ChannelFuture cf = boot.connect(websocketURI.getHost(), port).sync();
//            cf.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    if(cf.isSuccess()){
//                        System.out.println("连接成功");
//                    }else {
//                        System.out.println("连接失败");
//                    }
//                }
//            });
//            final Channel channel = cf.channel();
            handShaker.handshake(channel).sync();
//            sync.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    if(cf.isSuccess()){
//                        System.out.println("握手成功");
//                    }else {
//                        System.out.println("握手失败");
//                    }
//                }
//            });
//            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("连接websocket异常:"+ e);
        }finally {
//            group.shutdownGracefully();
        }
    }

}
