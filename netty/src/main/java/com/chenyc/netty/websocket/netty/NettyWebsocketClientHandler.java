package com.chenyc.netty.websocket.netty;

import com.alibaba.fastjson.JSONObject;
import com.chenyc.netty.custom_protocol.custom.server.LoginAuthRespHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.zip.GZIPInputStream;

/**
 * @author chenyc
 * @create 2020-09-09 10:38
 */
public class NettyWebsocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyWebsocketClientHandler.class);

    private  WebSocketClientHandshaker handShaker;
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("websocket连接断开");
    }

    public NettyWebsocketClientHandler(WebSocketClientHandshaker handShaker) {
        this.handShaker = handShaker;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        Channel ch = ctx.channel();
        if (!this.handShaker.isHandshakeComplete()) {
            FullHttpResponse response = (FullHttpResponse) obj;
            //握手协议返回，设置结束握手
            this.handShaker.finishHandshake(ch, response);
            System.out.println("连接websocket成功, remoteAddress="+ch.remoteAddress());

            //订阅数据
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sub", "market.ethbtc.kline.1min");
            jsonObject.put("id", "id1");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(jsonObject.toString()));

        } else if (obj instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) obj;
            //文本信息
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                System.out.println(textFrame.text());
            }
            //二进制信息
            if (frame instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame)frame;
                ByteBuf content = binFrame.content();
                byte[] bytes = new byte[content.readableBytes()];
                content.readBytes(bytes);

                GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
                InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, "UTF-8");
                BufferedReader in2 = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String s;
                while ((s = in2.readLine()) != null) {
                    builder.append(s);
                }
                LOGGER.info("数据：{}",builder.toString());

                JSONObject jsonObject = JSONObject.parseObject(builder.toString());
                Long ping = jsonObject.getLong("ping");
                if (ping!=null) {
                    JSONObject resp = new JSONObject();
                    resp.put("pong", ping);
                    LOGGER.info("{}",resp.toString());
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(resp.toString()));
                }
            }
            //ping信息
            if (frame instanceof PongWebSocketFrame) {
                System.out.println("WebSocket Client received pong");
            }
            //关闭消息
            if (frame instanceof CloseWebSocketFrame) {
                System.out.println("receive close frame");
                ch.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("cause==="+cause);
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved被调用"+ctx.channel().id().asLongText());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            //将evt向下转型为IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType=null;
            switch (event.state()){
                case READER_IDLE:
                    eventType="du kong xian ";
                    break;
                case WRITER_IDLE:
                    eventType="xie kong xian ";
                    break;
                case ALL_IDLE:
                    eventType="duxie kong xian ";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress()+"==超时=="+eventType);
        }
    }
}
