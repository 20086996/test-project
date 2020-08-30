package com.chenyc.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class NettyByteBuf01 {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,北京", CharsetUtil.UTF_8);
        if(byteBuf.hasArray()){
            byte[] array = byteBuf.array();

            System.out.println(new String(array,CharsetUtil.UTF_8));

            System.out.println(byteBuf);

            System.out.println(byteBuf.arrayOffset());//0
            System.out.println(byteBuf.readerIndex());//0
            System.out.println(byteBuf.writerIndex());//12
            System.out.println(byteBuf.capacity());//36

            System.out.println(byteBuf.readByte());//

            int leng = byteBuf.readableBytes();

            for (int i=0;i<leng;i++){
                System.out.println((char) byteBuf.getByte(i));
            }


            System.out.println(byteBuf.getCharSequence(0,4,CharsetUtil.UTF_8)
            );
        }
    }
}
