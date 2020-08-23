package com.chenyc.nio;

import java.nio.ByteBuffer;

public class ReasOnlyBuffer {

    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(64);
        for (int i=0;i<64;i++){
            allocate.put((byte) i);
        }

        allocate.flip();


        ByteBuffer byteBuffer = allocate.asReadOnlyBuffer();
        System.out.println(byteBuffer);

        while (byteBuffer.hasRemaining()){
            System.out.println(byteBuffer.get());
        }


        byteBuffer.put((byte) 100);
    }
}
