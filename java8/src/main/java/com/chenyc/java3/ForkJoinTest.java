package com.chenyc.java3;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.LongStream;

public class ForkJoinTest {

    /**
     * ForkJoin 框架
     */
    @Test
    public void test01(){
        Instant start = Instant.now();

        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinCalculate task = new ForkJoinCalculate(0, 900000000L);

        Long sum = pool.invoke(task);
        System.out.println(sum);

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end).toMillis());
    }

    /**
     * 普通 for循环
     */
    @Test
    public void test02(){
        Instant start = Instant.now();

        Long sum = 0L;
        for (long i = 0; i < 900000000L; i++) {
            sum += i;
        }

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end).toMillis());
    }

    /**
     * JAVA8   串行流
     */
    @Test
    public  void test3(){
        Instant start = Instant.now();
        long reduce = LongStream.rangeClosed(0, 900000000L)
                .reduce(0, Long::sum);
        Instant end = Instant.now();
        System.out.println(reduce);
        System.out.println(Duration.between(start, end).toMillis());
        /**
         *
         * 405000000450000000
         * 3021
         */

    }

    /**
     * JAVA8   并行流
     * //串行流(单线程)：切换为并行流 parallel()
 *     //并行流：切换为串行流 sequential()
     */
    @Test
    public  void test4(){
        Instant start = Instant.now();
        long reduce = LongStream.rangeClosed(0, 900000000L)
                .parallel()
                .reduce(0, Long::sum);
        Instant end = Instant.now();
        System.out.println(reduce);
        System.out.println(Duration.between(start, end).toMillis());
        /**
         *
         * 405000000450000000
         * 380
         */
    }

}