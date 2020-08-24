package com.chenyc.nio;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * 1、MappedByteBuffer可以让文件直接在内存中（堆外内存）修改，操作系统不需要拷贝一次
 */
public class MappedByteBuffer {
    @Test
    public void test01() throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("file05.txt", "rw");
        FileChannel channel = randomAccessFile.getChannel();
        /**
         * 参数1: FileChannel.MapMode.READ_WRITE，使用的读写模式
         * 参数2: 0，可以直接修改的起始位置
         * 参数3: 5，是映射到内存的大小(不是文件中字母的索引位置），即将 1.txt 的多少个字节映射到内存，也就是可以直接修改的范围就是 [0, 5)
         * 实际的实例化类型：DirectByteBuffer
         */
        java.nio.MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        map.put(0, (byte) 'H');
        randomAccessFile.close();
    }


}
