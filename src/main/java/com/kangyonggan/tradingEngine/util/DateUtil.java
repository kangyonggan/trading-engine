package com.kangyonggan.tradingEngine.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kyg
 */
public final class DateUtil {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE8_PATTERN = "yyyyMMdd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME14_PATTERN = "yyyyMMddHHmmss";

    private DateUtil() {}

    /**
     * 日期解析
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date) throws ParseException {
        return parseDate(date, DATE_PATTERN);
    }
    /**
     * 日期解析
     *
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(date);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

}
