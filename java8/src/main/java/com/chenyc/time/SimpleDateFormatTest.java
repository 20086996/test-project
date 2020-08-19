package com.chenyc.time;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author chenyc
 * @create 2020-08-15 15:37
 */
public class SimpleDateFormatTest {

    /**
     * 线程安全问题
     * */
    @Test
    public void test01() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Runnable task = ()-> {
            try {
                Date parse = simpleDateFormat.parse("20161218 01:01:01");
                System.out.println(parse);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        for (int i=0;i<=100;i++){
            Future<?> submit = pool.submit(task);
        }
        pool.shutdown();
    }


    @Test
    public void test02() throws Exception {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Runnable task = ()-> {
            String format = simpleDateFormat.format(now);
            System.out.println(format);
        };

        for (int i=0;i<=100;i++){
            pool.submit(task);
        }
    }


    /**
     * 修复线程安全问题
     * */
    @Test
    public void test03() throws Exception {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Runnable task = ()-> {
            try {
                Date parse = DateFormateThreadLocal.convert("20161218");
                System.out.println(parse);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        for (int i=0;i<=100;i++){
            Future<?> submit = pool.submit(task);
        }

        Thread.sleep(200);
        pool.shutdown();

    }


    /**
     * 1.8修复线程安全问题
     * */
    @Test
    public void test04() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Runnable task = ()-> {
            try {
                LocalDateTime parse = LocalDateTime.parse("20200815 01:01:01", dtf);
                System.out.println(parse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for (int i=0;i<=100;i++){
            Future<?> submit = pool.submit(task);
        }

        pool.shutdown();

    }
}
