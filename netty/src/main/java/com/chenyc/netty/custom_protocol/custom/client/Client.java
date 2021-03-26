package com.chenyc.netty.custom_protocol.custom.client;

import com.chenyc.netty.custom_protocol.NettyConstant;
import com.chenyc.netty.custom_protocol.custom.codec.NettyMessageDecoder;
import com.chenyc.netty.custom_protocol.custom.codec.NettyMessageEncoder;
import com.sun.org.apache.bcel.internal.classfile.Code;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {

	public static void main(String[] args) throws Exception {
		new Client().connect();
	}

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	//创建工作线程组
	EventLoopGroup group = new NioEventLoopGroup();

	public void connect() {
		// 配置客户端NIO线程组
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
							ch.pipeline().addLast(new NettyMessageEncoder());
							ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
							ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
							ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
						}
					});
			// 发起异步连接操作
			ChannelFuture future = b.connect(new InetSocketAddress(NettyConstant.REMOTEIP, NettyConstant.PORT),
					new InetSocketAddress(NettyConstant.LOCALIP, NettyConstant.LOCAL_PORT)).sync();
			
			//手动发测试数据，验证是否会产生TCP粘包/拆包情况
//			Channel c = future.channel();
//			
//			for (int i = 0; i < 500; i++) {
//				NettyMessage message = new NettyMessage();
//				Header header = new Header();
//				header.setSessionID(1001L);
//				header.setPriority((byte) 1);
//				header.setType((byte) 0);
//				message.setHeader(header);
//				message.setBody("我是请求数据" + i);
//				c.writeAndFlush(message);
//			}

			future.channel().closeFuture().sync();
		} catch (Exception e){
			reconnect();
		}
	}

	private void reconnect() {
//		if (!isDestroy) {
//			if (isInit && maxInitRetryAttempts <= 0) {
//				consumer.accept(R.fail(Code.SYSTEM_ERROR, String.format("连接websocket失败，已尝试%s次，请检查配置或确认服务端已启动！",
//						foundation.getMaxRetryAttemptsLimit())));
//			} else {
				group.schedule(this::connect, 5L, TimeUnit.SECONDS);
//			}
//		}
	}

}
