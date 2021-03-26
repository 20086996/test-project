package org.tinygame.herostory01;

import com.alibaba.fastjson.JSONArray;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 消息编码器
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == msg ||
            !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        // 获取消息类
        Class<?> msgClazz = msg.getClass();

        // 获取消息编码
        int msgCode = GameMsgRecognizer.getMsgCodeByMsgClazz(msgClazz);
        if (msgCode <= -1) {
            LOGGER.error(
                "无法识别的消息, msgClazz = {}",
                msgClazz.getName()
            );
            return;
        }

        // 获取消息体字节数组
        byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeShort((short) 0); // 写出消息长度, 目前写出 0 只是为了占位
        byteBuf.writeShort((short) msgCode); // 写出消息编号
        byteBuf.writeBytes(msgBody); // 写出消息体

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx, frame, promise);
    }

    public static void main(String[] args) {
        JSONArray list = new JSONArray();
        JSONArray objects = new JSONArray();
        objects.add("a");
        objects.add("b");
        objects.add("c");
        list.add(objects);
        JSONArray objects1 = new JSONArray();
        objects1.add("d");
        objects1.add("e");
        objects1.add("f");
        list.add(objects1);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("a", "命中第一个");
        hashMap.put("b", "命中第二个");
        hashMap.put("d", "命中第三个");
        System.out.println(list);
        System.out.println(hashMap);


        BiFunction<JSONArray,Map<String,String>,String> name =(jsonarray,map)
                ->jsonarray.stream().map(a->JSONArray.parseArray(a.toString()))
                                    .map(as->as.stream().map(map::get).collect(Collectors.joining("-")))
                                    .collect(Collectors.joining(", "));
        String apply = name.apply(list, hashMap);
        System.out.println(apply);

//        BiFunction<JSONArray,Map<String,String>,String> name1 =(jsonarray,map)
//                ->jsonarray.stream().map(map::get).collect(Collectors.joining("-"));
//        String apply1 = name1.apply(list, hashMap);
//        System.out.println(apply1);
    }
}
