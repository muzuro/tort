package com.mzr.tort.core.domain;


import org.apache.commons.lang3.time.DateUtils;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 *
 * @date 08/12/16
 */
public final class DateHelper {

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm");

    private static final DateFormat FULL_TIME_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");

    private static final DateFormat FULL_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static Date tomorrow() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static GregorianCalendar toGregorianCalendar(Time time) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(time.getTime());
        return gregorianCalendar;
    }

    public static GregorianCalendar toGregorianCalendar(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return gregorianCalendar;
    }

    public static GregorianCalendar toGregorianCalendar(LocalDate date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return gregorianCalendar;
    }

    public static GregorianCalendar toGregorianCalendar(Long aMillis) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date(aMillis));
        return gregorianCalendar;
    }


    public static GregorianCalendar createYearMonth(int year, int month) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        return calendar;
    }

    public static DateFormat timeFormat() {
        return (DateFormat) TIME_FORMAT.clone();
    }

    public static DateFormat dateTimeFormat() {
        return (DateFormat) DATE_TIME_FORMAT.clone();
    }

    public static DateFormat fullTimeFormat() {
        return (DateFormat) FULL_TIME_FORMAT.clone();
    }

    public static DateFormat dateFormat() {
        return (DateFormat) DATE_FORMAT.clone();
    }

    public static DateFormat fullDateFormat() {
        return (DateFormat) FULL_DATE_FORMAT.clone();
    }

    public static String formatTimeInterval(Date start, Date end) {
        StringBuilder sb = new StringBuilder();
        sb.append(timeFormat().format(start));
        sb.append(" - ");
        sb.append(timeFormat().format(end));
        return sb.toString();
    }


    public static boolean between(Date time, Date low, Date high) {
        return time.after(low) && time.before(high);
    }

    public static boolean betweenEq(Date time, Date low, Date high) {
        return (time.after(low) || time.equals(low)) && (time.before(high) || time.equals(high));
    }

    /**
     * Входит ли промежуток времени A в промежуток времени B
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean between(Interval a, Interval b) {
        if (Objects.equals(a.getStart().getTime(), b.getStart().getTime()) && Objects.equals(a.getEnd().getTime(), b.getEnd().getTime())) {
            return true;
        }

        if (a.getStart().getTime() <= a.getEnd().getTime() && b.getStart().getTime() <= b.getEnd().getTime()) {
            // a = 09-10, b = 7-21
            return a.getStart().getTime() >= b.getStart().getTime() && a.getStart().getTime() <= b.getEnd().getTime() && a.getEnd().getTime() >= b.getStart().getTime() && a.getEnd().getTime() <= b.getEnd().getTime();
        } else if (a.getStart().getTime() >= a.getEnd().getTime() && b.getStart().getTime() >= b.getEnd().getTime()) {
            // a = 21-06, b = 20-08
            return a.getStart().getTime() >= b.getEnd().getTime() && a.getStart().getTime() >= b.getStart().getTime() && a.getEnd().getTime() <= b.getEnd().getTime() && a.getEnd().getTime() <= b.getStart().getTime();
        } else {
            // У одного из интервалов время начала больше время окончания
            long aStart = a.getStart().getTime();
            long aEnd = a.getEnd().getTime();

            long bStart = b.getStart().getTime();
            long bEnd = b.getEnd().getTime();

            if (a.getStart().after(a.getEnd())) {
                // a = 20-8, b = 9-15 =>  a = 20-32 (добавили 24 часа), b = 9-15
                aEnd = aEnd + 24 * DateUtils.MILLIS_PER_HOUR;
                return aStart >= bStart && aStart <= bEnd && aEnd >= bStart && aEnd <= bEnd;
            } else if (b.getStart().after(b.getEnd())) {
                bEnd = bEnd + 24 * DateUtils.MILLIS_PER_HOUR;
                return aStart >= bStart && aStart <= bEnd && aEnd >= bStart && aEnd <= bEnd;
            }
        }

        return false;
    }

    /**
     * Может быть date, timestamp
     *
     * @param time1 Параметр 1
     * @param time2 Параметр 2
     * @return
     * @see java.util.Date
     * @see java.sql.Timestamp
     */
    public static boolean isEquals(Date time1, Date time2) {
        if (time1 != null && time2 != null) {
            return time1.getTime() == time2.getTime();
        }
        return Objects.equals(time1, time2);
    }

    /**
     * @param part Запрашиваемый интервал
     * @param full Разрешенный интервал
     * @return True, если запрашиваемый интервал входит в разрешенный интервал
     */
    public static boolean between(DateInterval part, DateInterval full) {
        if (betweenEq(part.getBegin(), full.getBegin(), full.getEnd()) && (betweenEq(part.getEnd(), full.getBegin(), full.getEnd()))) {
            return true;
        }
        return false;
    }
}

