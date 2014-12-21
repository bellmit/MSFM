package com.cboe.client.util;

/**
 * DateHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * Helper for working with Java Date object, primarily formatting them to any one of a number of speficied date formats.
 *
 */

import java.io.*;
import java.text.*;
import java.util.*;

import com.cboe.idl.cmiUtil.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public final class DateHelper
{
    public static final SimpleDateFormat static_logFormat             = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
    public static final SimpleDateFormat static_fixUTCDateFormat      = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat static_fixUTCTimeOnlyFormat  = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat static_fixUTCTimeStampFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
    public static final SimpleDateFormat static_fixLocalMktDateFormat = new SimpleDateFormat("yyyyMMdd");
    public static final TimeZone         TIMEZONE_UTC                 = TimeZone.getTimeZone("UTC");
    public static final int              TIMEZONE_OFFSET_UTC          = TIMEZONE_UTC.getRawOffset();
    public static final TimeZone         TIMEZONE_EST                 = TimeZone.getTimeZone("EST");
    public static final int              TIMEZONE_OFFSET_EST          = TIMEZONE_EST.getRawOffset();
    public static final TimeZone         TIMEZONE_CST                 = TimeZone.getTimeZone("CST");
    public static final int              TIMEZONE_OFFSET_CST          = TIMEZONE_CST.getRawOffset();
    public static final int              TIMEZONE_OFFSET_DEFAULT      = TimeZone.getDefault().getOffset(System.currentTimeMillis());

    public static final ThreadLocalDateStruct threadLocalDateStruct = new ThreadLocalDateStruct();
    public static final ThreadLocalTimeStruct threadLocalTimeStruct = new ThreadLocalTimeStruct();

    private static char[] utc_todayYYYYMMDD_char = new char[8];
    private static long   utc_todayStarts;
    private static long   utc_todayEnds;

    protected static final int JAN_1_1_JULIAN_DAY = 1721426; // January 1, 0001 (Gregorian)
    protected static final int EPOCH_JULIAN_DAY   = 2440588; // January 1, 1970 (Gregorian)
    protected static final int EPOCH_YEAR         = 1970;

    public static final int HOURS_PER_DAY                         = 24;
    public static final int MINUTES_PER_HOUR                      = 60;
    public static final int MINUTES_PER_DAY                       = MINUTES_PER_HOUR        * HOURS_PER_DAY;
    public static final int SECONDS_PER_MINUTE                    = 60;
    public static final int SECONDS_PER_HOUR                      = SECONDS_PER_MINUTE      * MINUTES_PER_HOUR;
    public static final int SECONDS_PER_DAY                       = SECONDS_PER_HOUR        * HOURS_PER_DAY;
    public static final int DAYS_PER_YEAR                         = 365;
    public static final long MILLISECONDS_PER_SECOND              = 1000;
    public static final long MILLISECONDS_PER_MINUTE              = MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE;
    public static final long MILLISECONDS_PER_HOUR                = MILLISECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long MILLISECONDS_PER_DAY                 = MILLISECONDS_PER_MINUTE * MINUTES_PER_DAY;

    public static final long MILLISECONDS_ON_JAN_1_1970           = 21600000L;

    private static final int MONTH_LENGTH[]                       = {0,31,28,31,30,31,30,31,31,30,31,30,31};      // 1-based
    private static final int LEAP_MONTH_LENGTH[]                  = {0,31,29,31,30,31,30,31,31,30,31,30,31};      // 1-based
    private static final int NUM_DAYS[]                           = {0,0,31,59,90,120,151,181,212,243,273,304,334}; // 1-based, for day-in-year
    private static final int LEAP_NUM_DAYS[]                      = {0,0,31,60,91,121,152,182,213,244,274,305,335}; // 1-based, for day-in-year

    protected static final String EMPTY_DATE_FORMAT               = "00000000";
    protected static final char[] EMPTY_DATE_FORMAT_CHARS         = {'0','0','0','0','0','0','0','0'};
    protected static final String EMPTY_TIMEONLY_FORMAT           = "00:00:00";
    protected static final char[] EMPTY_TIMEONLY_FORMAT_CHARS     = {'0','0',':','0','0',':','0','0'};
    protected static final String EMPTY_TIMESTAMP_FORMAT          = EMPTY_DATE_FORMAT + "-" + EMPTY_TIMEONLY_FORMAT;
    protected static final char[] EMPTY_TIMESTAMP_FORMAT_CHARS    = {'0','0','0','0','0','0','0','0','-','0','0',':','0','0',':','0','0'};

    public static class ThreadLocalDateStruct extends ThreadLocal
    {
        public Object     initialValue()  {return new DateStruct();}
        public DateStruct getDateStruct() {return (DateStruct) get();}
    }
    public static class ThreadLocalTimeStruct extends ThreadLocal
    {
        public Object     initialValue()  {return new TimeStruct();}
        public TimeStruct getTimeStruct() {return (TimeStruct) get();}
    }

    static
    {
        static_fixUTCDateFormat.setTimeZone(TIMEZONE_UTC);
        static_fixUTCTimeOnlyFormat.setTimeZone(TIMEZONE_UTC);
        static_fixUTCTimeStampFormat.setTimeZone(TIMEZONE_UTC);

        recomputeToday(Long.MAX_VALUE);
    }
//-------------------------------------------------------------------------------------------------------------------------------------

    public static final long floorDivide(long numerator, long denominator)
    {
        if ((numerator^denominator) >= 0 || numerator == 0)
        {
            // same sign, or 0/denominator, Java division does what we want
            return numerator / denominator;
        }

        // numerator and denominator have different signs
        return (numerator > 0) ? (numerator-1)/denominator - 1
                               : (numerator+1)/denominator - 1;
    }

    private static void recomputeToday(long millis)
    {
        if (millis >= utc_todayEnds)
        {
            synchronized(utc_todayYYYYMMDD_char)
            {
                if (millis < utc_todayEnds)
                {
                    return;
                }

                Calendar cal = GregorianCalendar.getInstance(TIMEZONE_UTC);

                makeYYYYMMDD(utc_todayYYYYMMDD_char, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));

                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE,      0);
                cal.set(Calendar.SECOND,      0);
                cal.set(Calendar.MILLISECOND, 0);

                utc_todayStarts = cal.getTime().getTime();

                cal.add(Calendar.DATE, 1);

                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE,      0);
                cal.set(Calendar.SECOND,      0);
                cal.set(Calendar.MILLISECOND, 0);

                utc_todayEnds = cal.getTime().getTime();

                cal = null;
            }
        }
    }

    public static long convertDaysToMilliseconds(int days)
    {
        return days * MILLISECONDS_PER_DAY;
    }
    public static long convertHoursToMilliseconds(int hours)
    {
        return hours * MILLISECONDS_PER_HOUR;
    }
    public static long convertHoursToSeconds(int hours)
    {
        return hours * SECONDS_PER_HOUR;
    }
    public static long convertMinutesToMilliseconds(int minutes)
    {
        return minutes * MILLISECONDS_PER_MINUTE;
    }
    public static int convertMinutesToSeconds(int minutes)
    {
        return minutes * SECONDS_PER_MINUTE;
    }
    public static int convertSecondsToHours(int seconds)
    {
        return seconds / SECONDS_PER_HOUR;
    }
    public static int convertSecondsToMinutes(int seconds)
    {
        return seconds / SECONDS_PER_MINUTE;
    }
    public static long convertSecondsToMilliseconds(int seconds)
    {
        return seconds * MILLISECONDS_PER_SECOND;
    }
    public static long convertSecondsToMilliseconds(float seconds)
    {
        return (long) (seconds * MILLISECONDS_PER_SECOND);
    }
    public static int convertMillisecondsToSeconds(long milliseconds)
    {
        return (int) (milliseconds / MILLISECONDS_PER_SECOND);
    }
    public static int convertMillisecondsToMinutes(long milliseconds)
    {
        return (int) (milliseconds / MILLISECONDS_PER_MINUTE);
    }
    public static int convertMillisecondsToHours(long milliseconds)
    {
        return (int) (milliseconds / MILLISECONDS_PER_HOUR);
    }
    public static int getMillisecondOfDay(long milliseconds)
    {
        return (int) ((milliseconds) % MILLISECONDS_PER_SECOND);
    }
    public static int getSecondOfDay(long milliseconds)
    {
        return (int) ((milliseconds) / MILLISECONDS_PER_SECOND) % SECONDS_PER_MINUTE;
    }
    public static int getMinuteOfDay(long milliseconds)
    {
        return (int) ((milliseconds) / MILLISECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
    }
    public static int getHourOfDay(long milliseconds)
    {
        return getHourOfDay(milliseconds, TIMEZONE_OFFSET_DEFAULT);
    }
    public static int getHourOfDay(long milliseconds, int timezoneOffset)
    {
        return (int) ((milliseconds + timezoneOffset) / MILLISECONDS_PER_HOUR ) % HOURS_PER_DAY;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int currentTimeInSeconds()
    {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static boolean isLeapYear(int year)
    {
        return ((year & 3) == 0) && ((year % 100 != 0) || (year % 400 == 0));
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean equals(DateStruct a, DateStruct b)
    {
        return a.year == b.year && a.month == b.month && a.day == b.day;
    }

    public static boolean equals(TimeStruct a, TimeStruct b)
    {
        return a.hour == b.hour && a.minute == b.minute && a.second == b.second && a.fraction == b.fraction;
    }

    public static boolean equalsHHMMSS(TimeStruct a, TimeStruct b)
    {
        return a.hour == b.hour && a.minute == b.minute && a.second == b.second;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static char[] makeYYYY(char[] buf, int year)
    {
        buf[0] = (char) ('0' + (char)  (year / 1000));
        buf[1] = (char) ('0' + (char) ((year / 100) % 10));
        buf[2] = (char) ('0' + (char) ((year % 100) / 10));
        buf[3] = (char) ('0' + (char)  (year % 10));

        return buf;
    }
    public static void makeYYYY(Writer writer, int year) throws Exception
    {
        writer.write((char) ('0' + (char)  (year / 1000)));
        writer.write((char) ('0' + (char) ((year / 100) % 10)));
        writer.write((char) ('0' + (char) ((year % 100) / 10)));
        writer.write((char) ('0' + (char)  (year % 10)));
    }
    public static void makeYYYY(FastCharacterWriter writer, int year)
    {
        writer.write((char) ('0' + (char)  (year / 1000)));
        writer.write((char) ('0' + (char) ((year / 100) % 10)));
        writer.write((char) ('0' + (char) ((year % 100) / 10)));
        writer.write((char) ('0' + (char)  (year % 10)));
    }
    public static char[] makeYYYYMMDD(char[] buf, int year, int month, int day)
    {
        buf[0] = (char) ('0' + (char)  (year / 1000));
        buf[1] = (char) ('0' + (char) ((year / 100) % 10));
        buf[2] = (char) ('0' + (char) ((year % 100) / 10));
        buf[3] = (char) ('0' + (char)  (year % 10));

        buf[4] = (char) ('0' + (char)  (month / 10));
        buf[5] = (char) ('0' + (char)  (month % 10));

        buf[6] = (char) ('0' + (char)  (day / 10));
        buf[7] = (char) ('0' + (char)  (day % 10));

        return buf;
    }
    public static void makeYYYYMMDD(Writer writer, int year, int month, int day) throws Exception
    {
        writer.write((char) ('0' + (char)  (year / 1000)));
        writer.write((char) ('0' + (char) ((year / 100) % 10)));
        writer.write((char) ('0' + (char) ((year % 100) / 10)));
        writer.write((char) ('0' + (char)  (year % 10)));

        writer.write((char) ('0' + (char)  (month / 10)));
        writer.write((char) ('0' + (char)  (month % 10)));

        writer.write((char) ('0' + (char)  (day / 10)));
        writer.write((char) ('0' + (char)  (day % 10)));
    }
    public static void makeYYYYMMDD(FastCharacterWriter writer, int year, int month, int day)
    {
        writer.write((char) ('0' + (char)  (year / 1000)));
        writer.write((char) ('0' + (char) ((year / 100) % 10)));
        writer.write((char) ('0' + (char) ((year % 100) / 10)));
        writer.write((char) ('0' + (char)  (year % 10)));

        writer.write((char) ('0' + (char)  (month / 10)));
        writer.write((char) ('0' + (char)  (month % 10)));

        writer.write((char) ('0' + (char)  (day / 10)));
        writer.write((char) ('0' + (char)  (day % 10)));
    }
    public static char[] makeYYYYMMDD(char[] buf, DateStruct dateStruct)
    {
        buf[0] = (char) ('0' + (char)  (dateStruct.year / 1000));
        buf[1] = (char) ('0' + (char) ((dateStruct.year / 100) % 10));
        buf[2] = (char) ('0' + (char) ((dateStruct.year % 100) / 10));
        buf[3] = (char) ('0' + (char)  (dateStruct.year % 10));

        buf[4] = (char) ('0' + (char)  (dateStruct.month / 10));
        buf[5] = (char) ('0' + (char)  (dateStruct.month % 10));

        buf[6] = (char) ('0' + (char)  (dateStruct.day / 10));
        buf[7] = (char) ('0' + (char)  (dateStruct.day % 10));

        return buf;
    }
    public static void makeYYYYMMDD(Writer writer, DateStruct dateStruct) throws Exception
    {
        writer.write((char) ('0' + (char)  (dateStruct.year / 1000)));
        writer.write((char) ('0' + (char) ((dateStruct.year / 100) % 10)));
        writer.write((char) ('0' + (char) ((dateStruct.year % 100) / 10)));
        writer.write((char) ('0' + (char)  (dateStruct.year % 10)));

        writer.write((char) ('0' + (char)  (dateStruct.month / 10)));
        writer.write((char) ('0' + (char)  (dateStruct.month % 10)));

        writer.write((char) ('0' + (char)  (dateStruct.day / 10)));
        writer.write((char) ('0' + (char)  (dateStruct.day % 10)));
    }
    public static void makeYYYYMMDD(FastCharacterWriter writer, DateStruct dateStruct)
    {
        writer.write((char) ('0' + (char)  (dateStruct.year / 1000)));
        writer.write((char) ('0' + (char) ((dateStruct.year / 100) % 10)));
        writer.write((char) ('0' + (char) ((dateStruct.year % 100) / 10)));
        writer.write((char) ('0' + (char)  (dateStruct.year % 10)));

        writer.write((char) ('0' + (char)  (dateStruct.month / 10)));
        writer.write((char) ('0' + (char)  (dateStruct.month % 10)));

        writer.write((char) ('0' + (char)  (dateStruct.day / 10)));
        writer.write((char) ('0' + (char)  (dateStruct.day % 10)));
    }
    public static char[] makeYYYYMMDD(char[] buf, int offset, int year, int month, int day)
    {
        buf[offset + 0] = (char) ('0' + (char)  (year / 1000));
        buf[offset + 1] = (char) ('0' + (char) ((year / 100) % 10));
        buf[offset + 2] = (char) ('0' + (char) ((year % 100) / 10));
        buf[offset + 3] = (char) ('0' + (char)  (year % 10));

        buf[offset + 4] = (char) ('0' + (char)  (month / 10));
        buf[offset + 5] = (char) ('0' + (char)  (month % 10));

        buf[offset + 6] = (char) ('0' + (char)  (day / 10));
        buf[offset + 7] = (char) ('0' + (char)  (day % 10));

        return buf;
    }
    public static char[] makeYYYYMMDD(char[] buf, int offset, DateStruct dateStruct)
    {
        buf[offset + 0] = (char) ('0' + (char)  (dateStruct.year / 1000));
        buf[offset + 1] = (char) ('0' + (char) ((dateStruct.year / 100) % 10));
        buf[offset + 2] = (char) ('0' + (char) ((dateStruct.year % 100) / 10));
        buf[offset + 3] = (char) ('0' + (char)  (dateStruct.year % 10));

        buf[offset + 4] = (char) ('0' + (char)  (dateStruct.month / 10));
        buf[offset + 5] = (char) ('0' + (char)  (dateStruct.month % 10));

        buf[offset + 6] = (char) ('0' + (char)  (dateStruct.day / 10));
        buf[offset + 7] = (char) ('0' + (char)  (dateStruct.day % 10));

        return buf;
    }
    public static char[] makeHHMMSSsss(char[] buf, int offset, long millis, int timezoneOffset)
    {
        int n = getHourOfDay(millis, timezoneOffset);
        buf[offset + 0] = (char) ('0' + (char) (n / 10));
        buf[offset + 1] = (char) ('0' + (char) (n % 10));

        buf[offset + 2] = ':';

        n = getMinuteOfDay(millis);
        buf[offset + 3] = (char) ('0' + (char) (n / 10));
        buf[offset + 4] = (char) ('0' + (char) (n % 10));

        buf[offset + 5] = ':';

        n = getSecondOfDay(millis);
        buf[offset + 6] = (char) ('0' + (char) (n / 10));
        buf[offset + 7] = (char) ('0' + (char) (n % 10));

        buf[offset + 8] = '.';

        n = getMillisecondOfDay(millis);
        buf[offset + 9]  = (char) ('0' + (char) (n / 100));
        buf[offset + 10] = (char) ('0' + (char) ((n / 10) % 10));
        buf[offset + 11] = (char) ('0' + (char) (n % 10));

        return buf;
    }
    public static char[] makeHHMMSSsss(char[] buf, int offset, long millis)
    {
        int n = getHourOfDay(millis);
        buf[offset + 0] = (char) ('0' + (char) (n / 10));
        buf[offset + 1] = (char) ('0' + (char) (n % 10));

        buf[offset + 2] = ':';

        n = getMinuteOfDay(millis);
        buf[offset + 3] = (char) ('0' + (char) (n / 10));
        buf[offset + 4] = (char) ('0' + (char) (n % 10));

        buf[offset + 5] = ':';

        n = getSecondOfDay(millis);
        buf[offset + 6] = (char) ('0' + (char) (n / 10));
        buf[offset + 7] = (char) ('0' + (char) (n % 10));

        buf[offset + 8] = '.';

        n = getMillisecondOfDay(millis);
        buf[offset + 9]  = (char) ('0' + (char) (n / 100));
        buf[offset + 10] = (char) ('0' + (char) ((n / 10) % 10));
        buf[offset + 11] = (char) ('0' + (char) (n % 10));

        return buf;
    }
    public static void makeHHMMSSsss(PrintStream stream, long millis)
    {
        if (millis <= 0)
        {
            stream.print(EMPTY_TIMEONLY_FORMAT_CHARS);
            return;
        }

        int n = getHourOfDay(millis);
        stream.write((char) ('0' + (char) (n / 10)));
        stream.write((char) ('0' + (char) (n % 10)));

        stream.write(':');

        n = getMinuteOfDay(millis);
        stream.write((char) ('0' + (char) (n / 10)));
        stream.write((char) ('0' + (char) (n % 10)));

        stream.write(':');

        n = getSecondOfDay(millis);
        stream.write((char) ('0' + (char) (n / 10)));
        stream.write((char) ('0' + (char) (n % 10)));

        stream.write('.');

        n = getMillisecondOfDay(millis);
        stream.write((char) ('0' + (char) (n / 100)));
        stream.write((char) ('0' + (char) ((n / 10) % 10)));
        stream.write((char) ('0' + (char) (n % 10)));
    }
    public static void makeHHMMSS(PrintStream stream, long millis)
    {
        if (millis <= 0)
        {
            stream.print(EMPTY_TIMEONLY_FORMAT_CHARS);
            return;
        }

        int n = getHourOfDay(millis);
        stream.write((char) ('0' + (char) (n / 10)));
        stream.write((char) ('0' + (char) (n % 10)));

        stream.write(':');

        n = getMinuteOfDay(millis);
        stream.write((char) ('0' + (char) (n / 10)));
        stream.write((char) ('0' + (char) (n % 10)));

        stream.write(':');

        n = getSecondOfDay(millis);
        stream.write((char) ('0' + (char) (n / 10)));
        stream.write((char) ('0' + (char) (n % 10)));
    }
    public static char[] makeHHMMSSsss(long millis)
    {
        if (millis <= 0)
        {
            return EMPTY_TIMEONLY_FORMAT_CHARS;
        }

        char[] buf = new char[12];

        int n = getHourOfDay(millis);
        buf[0] = (char) ('0' + (char) (n / 10));
        buf[1] = (char) ('0' + (char) (n % 10));

        buf[2] = ':';

        n = getMinuteOfDay(millis);
        buf[3] = (char) ('0' + (char) (n / 10));
        buf[4] = (char) ('0' + (char) (n % 10));

        buf[5] = ':';

        n = getSecondOfDay(millis);
        buf[6] = (char) ('0' + (char) (n / 10));
        buf[7] = (char) ('0' + (char) (n % 10));

        buf[8] = '.';

        n = getMillisecondOfDay(millis);
        buf[9]  = (char) ('0' + (char) (n / 100));
        buf[10] = (char) ('0' + (char) ((n / 10) % 10));
        buf[11] = (char) ('0' + (char) (n % 10));

        return buf;
    }
    public static char[] makeHHMMSSsss(long millis, int timezoneOffset)
    {
        if (millis <= 0)
        {
            return EMPTY_TIMEONLY_FORMAT_CHARS;
        }

        char[] buf = new char[12];

        int n = getHourOfDay(millis, timezoneOffset);
        buf[0] = (char) ('0' + (char) (n / 10));
        buf[1] = (char) ('0' + (char) (n % 10));

        buf[2] = ':';

        n = getMinuteOfDay(millis);
        buf[3] = (char) ('0' + (char) (n / 10));
        buf[4] = (char) ('0' + (char) (n % 10));

        buf[5] = ':';

        n = getSecondOfDay(millis);
        buf[6] = (char) ('0' + (char) (n / 10));
        buf[7] = (char) ('0' + (char) (n % 10));

        buf[8] = '.';

        n = getMillisecondOfDay(millis);
        buf[9]  = (char) ('0' + (char) (n / 100));
        buf[10] = (char) ('0' + (char) ((n / 10) % 10));
        buf[11] = (char) ('0' + (char) (n % 10));

        return buf;
    }
    public static void makeHHMMSSsss(Writer writer, long millis, int timezoneOffset) throws Exception
    {
        if (millis <= 0)
        {
            writer.write(EMPTY_TIMEONLY_FORMAT_CHARS);
            return;
        }

        int n = getHourOfDay(millis, timezoneOffset);
        writer.write((char) ('0' + (char) (n / 10)));
        writer.write((char) ('0' + (char) (n % 10)));

        writer.write(':');

        n = getMinuteOfDay(millis);
        writer.write((char) ('0' + (char) (n / 10)));
        writer.write((char) ('0' + (char) (n % 10)));

        writer.write(':');

        n = getSecondOfDay(millis);
        writer.write((char) ('0' + (char) (n / 10)));
        writer.write((char) ('0' + (char) (n % 10)));

        writer.write('.');

        n = getMillisecondOfDay(millis);
        writer.write((char) ('0' + (char) (n / 100)));
        writer.write((char) ('0' + (char) ((n / 10) % 10)));
        writer.write((char) ('0' + (char) (n % 10)));
    }

    public static void makeHHMMSSsss(FastCharacterWriter writer, long millis, int timezoneOffset)
    {
        if (millis <= 0)
        {
            writer.write(EMPTY_TIMEONLY_FORMAT_CHARS);
            return;
        }

        int n = getHourOfDay(millis, timezoneOffset);
        writer.write((char) ('0' + (char) (n / 10)));
        writer.write((char) ('0' + (char) (n % 10)));

        writer.write(':');

        n = getMinuteOfDay(millis);
        writer.write((char) ('0' + (char) (n / 10)));
        writer.write((char) ('0' + (char) (n % 10)));

        writer.write(':');

        n = getSecondOfDay(millis);
        writer.write((char) ('0' + (char) (n / 10)));
        writer.write((char) ('0' + (char) (n % 10)));

        writer.write('.');

        n = getMillisecondOfDay(millis);
        writer.write((char) ('0' + (char) (n / 100)));
        writer.write((char) ('0' + (char) ((n / 10) % 10)));
        writer.write((char) ('0' + (char) (n % 10)));
    }

    public static char[] makeHHMMSS(char[] buf, int hour, int minute, int second)
    {
        buf[0] = (char) ('0' + (char) (hour / 10));
        buf[1] = (char) ('0' + (char) (hour % 10));

        buf[2] = ':';

        buf[3] = (char) ('0' + (char) (minute / 10));
        buf[4] = (char) ('0' + (char) (minute % 10));

        buf[5] = ':';

        buf[6] = (char) ('0' + (char) (second / 10));
        buf[7] = (char) ('0' + (char) (second % 10));

        return buf;
    }
    public static void makeHHMMSS(Writer writer, int hour, int minute, int second) throws Exception
    {
        writer.write((char) ('0' + (char) (hour / 10)));
        writer.write((char) ('0' + (char) (hour % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (minute / 10)));
        writer.write((char) ('0' + (char) (minute % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (second / 10)));
        writer.write((char) ('0' + (char) (second % 10)));
    }
    public static void makeHHMMSS(FastCharacterWriter writer, int hour, int minute, int second)
    {
        writer.write((char) ('0' + (char) (hour / 10)));
        writer.write((char) ('0' + (char) (hour % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (minute / 10)));
        writer.write((char) ('0' + (char) (minute % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (second / 10)));
        writer.write((char) ('0' + (char) (second % 10)));
    }
    public static void makeHHMMSSsss(FastCharacterWriter writer, int hour, int minute, int second, int millis)
    {
        writer.write((char) ('0' + (char) (hour / 10)));
        writer.write((char) ('0' + (char) (hour % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (minute / 10)));
        writer.write((char) ('0' + (char) (minute % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (second / 10)));
        writer.write((char) ('0' + (char) (second % 10)));

        writer.write('.');

        writer.write((char) ('0' + (char) (millis / 100)));
        writer.write((char) ('0' + (char) (millis / 10) % 10));
        writer.write((char) ('0' + (char) (millis % 10)));
    }
    public static char[] makeHHMMSS(char[] buf, TimeStruct timeStruct)
    {
        if (timeStruct.hour < 0)
        {
            return EMPTY_TIMEONLY_FORMAT_CHARS;
        }

        buf[0] = (char) ('0' + (char) (timeStruct.hour / 10));
        buf[1] = (char) ('0' + (char) (timeStruct.hour % 10));

        buf[2] = ':';

        buf[3] = (char) ('0' + (char) (timeStruct.minute / 10));
        buf[4] = (char) ('0' + (char) (timeStruct.minute % 10));

        buf[5] = ':';

        buf[6] = (char) ('0' + (char) (timeStruct.second / 10));
        buf[7] = (char) ('0' + (char) (timeStruct.second % 10));

        return buf;
    }
    public static void makeHHMMSS(Writer writer, TimeStruct timeStruct) throws Exception
    {
        if (timeStruct.hour < 0)
        {
            writer.write(EMPTY_TIMEONLY_FORMAT_CHARS);
            return;
        }

        writer.write((char) ('0' + (char) (timeStruct.hour / 10)));
        writer.write((char) ('0' + (char) (timeStruct.hour % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (timeStruct.minute / 10)));
        writer.write((char) ('0' + (char) (timeStruct.minute % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (timeStruct.second / 10)));
        writer.write((char) ('0' + (char) (timeStruct.second % 10)));
    }
    public static void makeHHMMSS(FastCharacterWriter writer, TimeStruct timeStruct)
    {
        if (timeStruct.hour < 0)
        {
            writer.write(EMPTY_TIMEONLY_FORMAT_CHARS);
            return;
        }

        writer.write((char) ('0' + (char) (timeStruct.hour / 10)));
        writer.write((char) ('0' + (char) (timeStruct.hour % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (timeStruct.minute / 10)));
        writer.write((char) ('0' + (char) (timeStruct.minute % 10)));

        writer.write(':');

        writer.write((char) ('0' + (char) (timeStruct.second / 10)));
        writer.write((char) ('0' + (char) (timeStruct.second % 10)));
    }
    public static char[] makeHHMMSS(char[] buf, int offset, int hour, int minute, int second)
    {
        buf[offset + 0] = (char) ('0' + (char) (hour / 10));
        buf[offset + 1] = (char) ('0' + (char) (hour % 10));

        buf[offset + 2] = ':';

        buf[offset + 3] = (char) ('0' + (char) (minute / 10));
        buf[offset + 4] = (char) ('0' + (char) (minute % 10));

        buf[offset + 5] = ':';

        buf[offset + 6] = (char) ('0' + (char) (second / 10));
        buf[offset + 7] = (char) ('0' + (char) (second % 10));

        return buf;
    }
    public static char[] makeHHMMSS(char[] buf, int offset, TimeStruct timeStruct)
    {
        if (timeStruct.hour < 0)
        {
            return EMPTY_TIMEONLY_FORMAT_CHARS;
        }

        buf[offset + 0] = (char) ('0' + (char) (timeStruct.hour / 10));
        buf[offset + 1] = (char) ('0' + (char) (timeStruct.hour % 10));

        buf[offset + 2] = ':';

        buf[offset + 3] = (char) ('0' + (char) (timeStruct.minute / 10));
        buf[offset + 4] = (char) ('0' + (char) (timeStruct.minute % 10));

        buf[offset + 5] = ':';

        buf[offset + 6] = (char) ('0' + (char) (timeStruct.second / 10));
        buf[offset + 7] = (char) ('0' + (char) (timeStruct.second % 10));

        return buf;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean validateYYYYMMDD(int yyyy, int mm, int dd)
    {
        if (yyyy < EPOCH_YEAR || yyyy >= 2400 || dd < 1 || mm < 1 || mm > 12)
        {
            return false;
        }

        if (isLeapYear(yyyy))
        {
            return dd <= LEAP_MONTH_LENGTH[mm];
        }
        else
        {
            return dd <= MONTH_LENGTH[mm];
        }
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    protected static DateStruct convertDateToThreadLocalDateStruct(long millis, int timezoneOffset)
    {
        return convertDateToDateStruct(millis, timezoneOffset, threadLocalDateStruct.getDateStruct());
    }
    protected static DateStruct convertDateToThreadLocalDateStruct(long millis)
    {
        return convertDateToDateStruct(millis, threadLocalDateStruct.getDateStruct());
    }
    protected static TimeStruct convertDateToThreadLocalTimeStruct(long millis, int timezoneOffset)
    {
        return convertDateToTimeStruct(millis, timezoneOffset, threadLocalTimeStruct.getTimeStruct());
    }
    protected static TimeStruct convertDateToThreadLocalTimeStruct(long millis)
    {
        return convertDateToTimeStruct(millis, threadLocalTimeStruct.getTimeStruct());
    }

    public static DateStruct convertDateToDateStruct(Date date, DateStruct dateStruct)
    {
        return convertDateToDateStruct(date.getTime(), TIMEZONE_OFFSET_UTC, dateStruct);
    }

    public static DateStruct convertDateToDateStruct(Date date, int timezoneOffset, DateStruct dateStruct)
    {
        return convertDateToDateStruct(date.getTime(), timezoneOffset, dateStruct);
    }

    public static DateStruct convertDateToDateStruct(long millis, DateStruct dateStruct)
    {
        return convertDateToDateStruct(millis, TIMEZONE_OFFSET_UTC, dateStruct);
    }

    public static DateStruct convertDateToDateStruct(long millis, int timezoneOffset, DateStruct dateStruct)
    {
        // algorithm taken from GregorianCalender.java in JDK1.4

        millis = EPOCH_JULIAN_DAY - JAN_1_1_JULIAN_DAY + floorDivide(millis + timezoneOffset, MILLISECONDS_PER_DAY);

        int n400       = (int) (millis / 146097);
        int dayOfYear  = (int) (millis % 146097);
        int n100       = dayOfYear / 36524;
            dayOfYear %= 36524;
        int n4         = dayOfYear / 1461;
            dayOfYear %= 1461;
        int n1         = dayOfYear / 365;
            dayOfYear %= 365;

        if (n100 == 4 || n1 == 4)
        {
            dateStruct.year = (short) (400 * n400 + 100 * n100 + 4 * n4 + n1);
            dayOfYear = 365; // Dec 31 at end of 4- or 400-yr cycle
        }
        else
        {
            dateStruct.year = (short) (400 * n400 + 100 * n100 + 4 * n4 + n1 + 1);
        }

        if (isLeapYear(dateStruct.year))
        {
            if (dayOfYear >= 60)
            {
                dateStruct.month = (byte) ((12 * (dayOfYear + 1) + 6) / 367 + 1);
            }
            else
            {
                dateStruct.month = (byte) ((12 * dayOfYear + 6) / 367 + 1);
            }

            dateStruct.day = (byte) (dayOfYear - LEAP_NUM_DAYS[dateStruct.month] + 1);
        }
        else
        {
            if (dayOfYear >= 59)
            {
                dateStruct.month = (byte) ((12 * (dayOfYear + 2) + 6) / 367 + 1);
            }
            else
            {
                dateStruct.month = (byte) ((12 * dayOfYear + 6) / 367 + 1);
            }

            dateStruct.day = (byte) (dayOfYear - NUM_DAYS[dateStruct.month] + 1);
        }

        return dateStruct;
    }
    public static DateStruct convertDateToDateStruct(long millis, int timezoneOffset)
    {
        return convertDateToDateStruct(millis, timezoneOffset, new DateStruct());
    }

    public static TimeStruct convertDateToTimeStruct(long millis, TimeStruct timeStruct)
    {
        timeStruct.hour   = (byte) getHourOfDay(millis);
        timeStruct.minute = (byte) getMinuteOfDay(millis);
        timeStruct.second = (byte) getSecondOfDay(millis);

        return timeStruct;
    }
    public static TimeStruct convertDateToTimeStruct(long millis, int timezoneOffset, TimeStruct timeStruct)
    {
        timeStruct.hour   = (byte) getHourOfDay(millis, timezoneOffset);
        timeStruct.minute = (byte) getMinuteOfDay(millis);
        timeStruct.second = (byte) getSecondOfDay(millis);

        return timeStruct;
    }

//-------------------------------------------------------------------------------------------------------------------------------------
    public static Date convertDateStructToDate(DateStruct dateStruct)
    {
        if (dateStruct == null)
        {
            return null;
        }

        return convertDateStructToDate(dateStruct, 0);
    }
    public static Date convertDateStructToDate(DateStruct dateStruct, int timezoneOffset)
    {
        if (dateStruct == null)
        {
            return null;
        }

        int elapsedYears = dateStruct.year - EPOCH_YEAR;

        return new Date(MILLISECONDS_ON_JAN_1_1970 + convertDaysToMilliseconds((elapsedYears * 365 + ((elapsedYears / 4))) + (isLeapYear(dateStruct.year) ? LEAP_NUM_DAYS[dateStruct.month] : NUM_DAYS[dateStruct.month]) + dateStruct.day - 1) - timezoneOffset);
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static DateStruct convertMaturityToDateStruct(int yyyymm, int dd)
    {
        return new DateStruct((byte) (yyyymm % 100), (byte) dd, (short) (yyyymm / 100));
    }

    public static DateStruct extractDateStruct(char[] array, int offset, int length)
    {
        if (length != 8)
        {
            return null;
        }

        int yyyy = 0;
        int mm   = 0;
        int dd   = 0;
        char b;

        b = array[offset++];
        if (b != '1' && b != '2') {return null;}
        yyyy = yyyy + (b - '0') * 1000;

        b = array[offset++];
        if (b != '9' && b != '0') {return null;}
        yyyy = yyyy + (b - '0') * 100;

        b = array[offset++];
        if (b < '0' || b > '9') {return null;}
        yyyy = yyyy + (b - '0') * 10;

        b = array[offset++];
        if (b < '0' || b > '9') {return null;}
        yyyy = yyyy + (b - '0');

        b = array[offset++];
        if (b < '0' || b > '1') {return null;}
        mm = mm + (b - '0') * 10;

        b = array[offset++];
        if (b < '0' || b > (mm == 0 ? '9' : '2')) {return null;}
        mm = mm + (b - '0');

        b = array[offset++];
        if (b < '0' || b > '3') {return null;}
        dd = dd + (b - '0') * 10;

        b = array[offset++];
        if (b < '0' || b > '9') {return null;}
        dd = dd + (b - '0');

        if (!validateYYYYMMDD(yyyy, mm, dd))
        {
            return null;
        }

        return new DateStruct((byte) mm, (byte) dd, (short) yyyy);
    }

    public static DateStruct extractDateStruct(String string)
    {
        if (string.length() != 8)
        {
            return null;
        }

        int yyyy = 0;
        int mm   = 0;
        int dd   = 0;
        char ch;

        ch = string.charAt(0);
        if (ch != '1' && ch != '2') {return null;}
        yyyy = yyyy + (ch - '0') * 1000;

        ch = string.charAt(1);
        if (ch != '9' && ch != '0') {return null;}
        yyyy = yyyy + (ch - '0') * 100;

        ch = string.charAt(2);
        if (ch < '0' || ch > '9') {return null;}
        yyyy = yyyy + (ch - '0') * 10;

        ch = string.charAt(3);
        if (ch < '0' || ch > '9') {return null;}
        yyyy = yyyy + (ch - '0');

        ch = string.charAt(4);
        if (ch < '0' || ch > '1') {return null;}
        mm = mm + (ch - '0') * 10;

        ch = string.charAt(5);
        if (ch < '0' || ch > (mm == 0 ? '9' : '2')) {return null;}
        mm = mm + (ch - '0');

        ch = string.charAt(6);
        if (ch < '0' || ch > '3') {return null;}
        dd = dd + (ch - '0') * 10;

        ch = string.charAt(7);
        if (ch < '0' || ch > '9') {return null;}
        dd = dd + (ch - '0');

        if (!validateYYYYMMDD(yyyy, mm, dd))
        {
            return null;
        }

        return new DateStruct((byte) mm, (byte) dd, (short) yyyy);
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static String stringizeDate(SimpleDateFormat format, Date date)
    {
        try
        {
            synchronized(format)
            {
                return format.format(date);
            }
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }

        return null;
    }

    public static Date extractDate(SimpleDateFormat format, String date_string)
    {
        try
        {
            synchronized(format)
            {
                return format.parse(date_string);
            }
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }

        return null;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static String stringizeDateInLogFormat()
    {
        return stringizeDateInLogFormat(System.currentTimeMillis());
    }
    public static String stringizeDateInLogFormat(Date date)
    {
        return stringizeDateInLogFormat(date.getTime());
    }
    public static String stringizeDateInLogFormat(long millis)
    {
        char[] buf = new char[21];

        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_DEFAULT);

        makeYYYYMMDD(buf, dateStruct);
        buf[8] = '-';
        makeHHMMSSsss(buf, 9, millis, TIMEZONE_OFFSET_DEFAULT);

        return StringHelper.newString(buf);
    }
    public static void appendDateInLogFormat(FastCharacterWriter writer, long millis)
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_DEFAULT);

        makeYYYYMMDD(writer, dateStruct);
        writer.write('-');
        makeHHMMSSsss(writer, millis, TIMEZONE_OFFSET_DEFAULT);
    }

//-------------------------------------------------------------------------------------------------------------------------------------
    public static Date extractDateInFixUTCDateFormat(char[] array, int offset, int length)
    {
        return extractDate(static_fixUTCDateFormat, StringHelper.newString(array, offset, length));
    }
    public static Date extractDateInFixUTCDateFormat(String date_string)
    {
        return extractDate(static_fixUTCDateFormat, date_string);
    }
    public static String stringizeDateInFixUTCDateFormat()
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(System.currentTimeMillis(), TIMEZONE_OFFSET_UTC);

        return StringHelper.newString(makeYYYYMMDD(new char[8], dateStruct));
    }
    public static String stringizeDateInFixUTCDateFormat(Date date)
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(date.getTime(), TIMEZONE_OFFSET_UTC);

        return StringHelper.newString(makeYYYYMMDD(new char[8], dateStruct));
    }
    public static String stringizeDateInFixUTCDateFormat(long millis)
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_UTC);

        return StringHelper.newString(makeYYYYMMDD(new char[8], dateStruct));
    }
    public static void appendDateInFixUTCDateFormat(FastCharacterWriter writer, DateStruct dateStruct)
    {
        makeYYYYMMDD(writer, dateStruct);
    }
    public static void appendDateInFixUTCDateFormat(Writer writer, DateStruct dateStruct) throws Exception
    {
        makeYYYYMMDD(writer, dateStruct);
    }
    public static void appendDateInFixUTCDateFormat(FastCharacterWriter writer, long millis)
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_UTC);

        makeYYYYMMDD(writer, dateStruct);
    }
    public static void appendDateInFixUTCDateFormat(Writer writer, long millis) throws Exception
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_UTC);

        makeYYYYMMDD(writer, dateStruct);
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static Date extractDateInFixUTCTimeOnlyFormat(char[] array, int offset, int length)
    {
        return extractDate(static_fixUTCTimeOnlyFormat, StringHelper.newString(array, offset, length));
    }
    public static Date extractDateInFixUTCTimeOnlyFormat(String date_string)
    {
        return extractDate(static_fixUTCTimeOnlyFormat, date_string);
    }
    public static String stringizeDateInFixUTCTimeOnlyFormat()
    {
        TimeStruct timeStruct = convertDateToThreadLocalTimeStruct(System.currentTimeMillis(), TIMEZONE_OFFSET_UTC);

        return StringHelper.newString(makeHHMMSS(new char[8], timeStruct));
    }
    public static String stringizeDateInFixUTCTimeOnlyFormat(Date date)
    {
        TimeStruct timeStruct = convertDateToThreadLocalTimeStruct(date.getTime(), TIMEZONE_OFFSET_UTC);

        return StringHelper.newString(makeHHMMSS(new char[8], timeStruct));
    }
    public static String stringizeDateInFixUTCTimeOnlyFormat(long millis)
    {
        TimeStruct timeStruct = convertDateToThreadLocalTimeStruct(millis, TIMEZONE_OFFSET_UTC);

        return StringHelper.newString(makeHHMMSS(new char[8], timeStruct));
    }
    public static void appendDateInFixUTCTimeOnlyFormat(FastCharacterWriter writer, long millis)
    {
        TimeStruct timeStruct = convertDateToThreadLocalTimeStruct(millis, TIMEZONE_OFFSET_UTC);

        makeHHMMSS(writer, timeStruct);
    }
    public static void appendDateInFixUTCTimeOnlyFormat(Writer writer, long millis) throws Exception
    {
        TimeStruct timeStruct = convertDateToThreadLocalTimeStruct(millis, TIMEZONE_OFFSET_UTC);

        makeHHMMSS(writer, timeStruct);
    }
    public static void appendDateInFixUTCTimeOnlyFormat(FastCharacterWriter writer, TimeStruct timeStruct)
    {
        makeHHMMSS(writer, timeStruct);
    }
    public static void appendDateInFixUTCTimeOnlyFormat(Writer writer, TimeStruct timeStruct) throws Exception
    {
        makeHHMMSS(writer, timeStruct);
    }
    public static String stringizeDateInFixUTCTimeOnlyFormat(TimeStruct timeStruct)
    {
        if (timeStruct.hour < 0)
        {
            return EMPTY_TIMEONLY_FORMAT;
        }

        return StringHelper.newString(makeHHMMSS(new char[8], timeStruct.hour, timeStruct.minute, timeStruct.second));
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static Date extractDateInFixUTCTimeStampFormat(char[] array, int offset, int length)
    {
        return extractDate(static_fixUTCTimeStampFormat, StringHelper.newString(array, offset, length));
    }
    public static Date extractDateInFixUTCTimeStampFormat(String date_string)
    {
        return extractDate(static_fixUTCTimeStampFormat, date_string);
    }
    public static String stringizeDateInFixUTCTimeStampFormat(Date date)
    {
        return stringizeDateInFixUTCTimeStampFormat(date.getTime());
    }
    public static String stringizeDateInFixUTCTimeStampFormat(long millis)
    {
        char[] buf = new char[17];

        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_UTC);
        TimeStruct timeStruct = convertDateToThreadLocalTimeStruct(millis, TIMEZONE_OFFSET_UTC);

        makeYYYYMMDD(buf, dateStruct);

        buf[8] = '-';

        return StringHelper.newString(makeHHMMSS(buf, 9, timeStruct));
    }
    public static void appendDateInFixUTCTimeStampFormat(FastCharacterWriter writer, DateStruct dateStruct, TimeStruct timeStruct)
    {
        makeYYYYMMDD(writer, dateStruct);
        writer.write('-');
        makeHHMMSS(writer, timeStruct);
    }
    public static void appendDateInFixUTCTimeStampFormat(Writer writer, DateStruct dateStruct, TimeStruct timeStruct) throws Exception
    {
        makeYYYYMMDD(writer, dateStruct);
        writer.write('-');
        makeHHMMSS(writer, timeStruct);
    }
    public static void appendDateInFixUTCTimeStampFormat(FastCharacterWriter writer, long millis)
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_UTC);
        TimeStruct timeStruct = convertDateToThreadLocalTimeStruct(millis, TIMEZONE_OFFSET_UTC);

        makeYYYYMMDD(writer, dateStruct);
        writer.write('-');
        makeHHMMSS(writer, timeStruct);
    }
    public static void appendDateInFixUTCTimeStampFormat(Writer writer, long millis) throws Exception
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_UTC);
        TimeStruct timeStruct = convertDateToThreadLocalTimeStruct(millis, TIMEZONE_OFFSET_UTC);

        makeYYYYMMDD(writer, dateStruct);
        writer.write('-');
        makeHHMMSS(writer, timeStruct);
    }
    public static String stringizeDateInFixUTCTimeStampFormat()
    {
        long now = System.currentTimeMillis();

        recomputeToday(now);

        now -= (utc_todayStarts - TIMEZONE_OFFSET_UTC);

        char[] buf = new char[17];

        System.arraycopy(utc_todayYYYYMMDD_char, 0, buf, 0, 8);

        buf[8] = '-';

        return StringHelper.newString(makeHHMMSS(buf, 9, getHourOfDay(now, TIMEZONE_OFFSET_UTC), getMinuteOfDay(now), getSecondOfDay(now)));
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static Date extractDateInFixLocalMktDateFormat(char[] array, int offset, int length)
    {
        if (length != 8)
        {
            return null;
        }

        return convertDateStructToDate(extractDateStruct(array, offset, length));
    }
    public static Date extractDateInFixLocalMktDateFormat(String date_string)
    {
        return convertDateStructToDate(extractDateStruct(date_string));
    }
    public static String stringizeDateInFixLocalMktDateFormat()
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(System.currentTimeMillis(), TIMEZONE_OFFSET_DEFAULT);

        return StringHelper.newString(makeYYYYMMDD(new char[8], dateStruct));
    }
    public static String stringizeDateInFixLocalMktDateFormat(Date date)
    {
        if (date == null)
        {
            return EMPTY_DATE_FORMAT;
        }

        DateStruct dateStruct = convertDateToThreadLocalDateStruct(date.getTime(), TIMEZONE_OFFSET_DEFAULT);

        return StringHelper.newString(makeYYYYMMDD(new char[8], dateStruct));
    }
    public static String stringizeDateInFixLocalMktDateFormat(long millis)
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_DEFAULT);

        return StringHelper.newString(makeYYYYMMDD(new char[8], dateStruct));
    }
    public static void appendDateInFixLocalMktDateFormat(FastCharacterWriter writer, DateStruct dateStruct)
    {
        makeYYYYMMDD(writer, dateStruct);
    }
    public static void appendDateInFixLocalMktDateFormat(Writer writer, DateStruct dateStruct) throws Exception
    {
        makeYYYYMMDD(writer, dateStruct);
    }
    public static void appendDateInFixLocalMktDateFormat(FastCharacterWriter writer, long millis)
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_DEFAULT);

        makeYYYYMMDD(writer, dateStruct);
    }
    public static void appendDateInFixLocalMktDateFormat(Writer writer, long millis) throws Exception
    {
        DateStruct dateStruct = convertDateToThreadLocalDateStruct(millis, TIMEZONE_OFFSET_DEFAULT);

        makeYYYYMMDD(writer, dateStruct);
    }
}
