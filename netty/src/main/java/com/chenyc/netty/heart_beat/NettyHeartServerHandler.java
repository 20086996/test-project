package com.chenyc.netty.heart_beat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyHeartServerHandler extends ChannelInboundHandlerAdapter {

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

            ctx.channel().close();
        }


    }
}
