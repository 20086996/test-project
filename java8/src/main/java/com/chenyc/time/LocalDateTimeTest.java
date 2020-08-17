package com.chenyc.time;

import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

/**
 * @author chenyc
 * @create 2020-08-15 16:25
 */
public class LocalDateTimeTest {

    /**
     * 人可读的格式
     * localDate 专门表示日期
     * localTime 专门表示时间
     * localDateTime 表示日期和时间
     * */
    @Test
    public void test01(){
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        /**2020-08-15T16:27:26.464*/

        LocalDateTime of = LocalDateTime.of(2020, 8, 15, 23, 23, 23);
        System.out.println(of);
        /**2020-08-15T23:23:23*/

        LocalDateTime localDateTime1 = localDateTime.plusYears(1);
        System.out.println(localDateTime1);
        /**2021-08-15T16:32:35.687*/

        LocalDateTime localDateTime2 = localDateTime.minusYears(1);
        System.out.println(localDateTime2);
        /**2019-08-15T16:33:47.546*/

        System.out.println(localDateTime.getYear());
        System.out.println(localDateTime.getMonthValue());
        System.out.println(localDateTime.getDayOfMonth());
        System.out.println(localDateTime.getHour());
        System.out.println(localDateTime.getMinute());
        System.out.println(localDateTime.getSecond());
        /**
         * 2020
         * 8
         * 15
         * 16
         * 56
         * 20
         * */
    }


    /**
     * Instant:时间戳
     * */
    @Test
    public  void test02(){
       // Instant：以 Unix 元年 1970-01-01 00:00:00 到某个时间之间的毫秒值
        Instant now = Instant.now();
        System.out.println(now);
        /**
         * 2020-08-15T08:58:56.833Z,默认获取UTC时区时间
         * */

        OffsetDateTime offsetDateTime = now.atOffset(ZoneOffset.ofHours(8));
        System.out.println(offsetDateTime);
        /**
         * 2020-08-15T17:01:00.185+08:00
         * */

        System.out.println(now.toEpochMilli());
        /**
         * 1597482158632
         * */

        Instant instant = Instant.ofEpochSecond(1);
        System.out.println(instant);
        /**1970-01-01T00:00:01Z*/
    }

    /**
     * Duration：计算两个时间之间的间隔
     * Period：计算两个日期之间的间隔
     * */
    @Test
    public void test03 () throws InterruptedException {
        Instant now = Instant.now();
        Thread.sleep(2000);
        Instant now1 = Instant.now();
        Duration between = Duration.between(now, now1);
        System.out.println(between.toMillis());
        //2000

        LocalTime now2 = LocalTime.now();
        Thread.sleep(2000);
        LocalTime now3 = LocalTime.now();
        Duration between1 = Duration.between(now2, now3);
        System.out.println(between1.toMillis());
        //2000

        LocalDate now4 = LocalDate.now();
        LocalDate of = LocalDate.of(2020, 1, 1);
        Period between2 = Period.between(of, now4);
        System.out.println(between2.getMonths());
        //7
    }

    /**
     * TemporalAdjusters：时间校正器,指定年月份等
     * */
    @Test
    public void test04(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        //2020-08-15T17:17:24.281

        LocalDateTime localDateTime = now.withDayOfMonth(10);
        System.out.println(localDateTime);
        //2020-08-10T17:17:24.281

        LocalDateTime with = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        LocalDateTime with1 = now.with(TemporalAdjusters.firstDayOfMonth());
        System.out.println(with);
        System.out.println(with1);
        //2020-08-16T17:20:10.506
        //2020-08-01T17:20:10.506

        //自定义
        LocalDateTime with2 = now.with((l) -> {
            LocalDateTime l1 = (LocalDateTime) l;
            DayOfWeek dayOfWeek = l1.getDayOfWeek();
            if (dayOfWeek.equals(DayOfWeek.SATURDAY)) {
                LocalDateTime localDateTime1 = l1.plusDays(1);
                return localDateTime1;
            }
            return l1.plusDays(2);
        });
        System.out.println(with2);
        //2020-08-16T17:27:06.699
    }


    /**
     * DateTimeFormatter 时间格式化
     * */
    @Test
    public void test05(){
        DateTimeFormatter isoDateTime = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime now = LocalDateTime.now();
        String format = now.format(isoDateTime);
        System.out.println(format);
        //2020-08-15T17:30:06.993

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
        String format1 = dateTimeFormatter.format(now);
        System.out.println(format1);
        //20200815 17:32:12

        LocalDateTime parse = LocalDateTime.parse(format1, dateTimeFormatter);
        System.out.println(parse);
        //2020-08-15T17:42:13
    }

    /**
     * ZonedDate
     * ZonedTime
     * ZonedDateTime
     * */
    @Test
    public void test06(){
        Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        availableZoneIds.forEach(System.out::println);

        //America/Marigot
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Marigot"));
        System.out.println(now);
        //2020-08-17T00:43:42.258


        LocalDateTime now1 = LocalDateTime.now(ZoneId.of("America/Marigot"));
        ZonedDateTime zonedDateTime = now1.atZone(ZoneId.of("America/Marigot"));
        System.out.println(zonedDateTime);
        //2020-08-17T00:46:19.619-04:00[America/Marigot]


    }
}
