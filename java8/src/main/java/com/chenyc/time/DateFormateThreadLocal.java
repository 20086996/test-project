package com.chenyc.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chenyc
 * @create 2020-08-15 16:00
 */
public class DateFormateThreadLocal {

    private static final ThreadLocal<DateFormat> df =new ThreadLocal<DateFormat>(){
        @Override
        protected  DateFormat initialValue(){
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    public static Date convert(String source) throws ParseException {
        return df.get().parse(source);
    }
}
