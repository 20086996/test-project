package com.chenyc.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

public class NIOByteBufferPutGet {

    @Test
    public void test01(){
        ByteBuffer allocate = ByteBuffer.allocate(64);
        allocate.putInt(100);
        allocate.putLong(9);
        allocate.putChar('上');
        allocate.putShort((short) 4);

        allocate.flip();

        System.out.println(allocate.getInt());
        System.out.println(allocate.getLong());
        System.out.println(allocate.getChar());
        System.out.println(allocate.getShort());
    }
}
