package com.cboe.client.util;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;  // annotation

public class DateHelperTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(DateHelperTest.class);
    }

    private final static String EMPTY_TIME = "00:00:00";
    private final static String EMPTY_DATE = "00000000";
    private final static TimeZone utcZone = TimeZone.getTimeZone("GMT+0");
    private final static TimeZone localZone = TimeZone.getDefault();
    private final static long msNow = System.currentTimeMillis();
    private final static int utcOffset = utcZone.getRawOffset();
    private final static int localOffset = localZone.getOffset(msNow);
    private final static Calendar utcCal = new GregorianCalendar(utcZone);
    private final static Calendar localCal = new GregorianCalendar(localZone);
    static
    {
        utcCal.setTimeInMillis(msNow);
        localCal.setTimeInMillis(msNow);
    }
    private final static int utcYear = utcCal.get(Calendar.YEAR);
    private final static int utcMonth = utcCal.get(Calendar.MONTH)+1;
    private final static int utcDay = utcCal.get(Calendar.DAY_OF_MONTH);
    private final static int utcHour = utcCal.get(Calendar.HOUR_OF_DAY);
    private final static int utcMinute = utcCal.get(Calendar.MINUTE);
    private final static int utcSecond = utcCal.get(Calendar.SECOND);
    private final static int utcMs = utcCal.get(Calendar.MILLISECOND);

    private final static int localYear = localCal.get(Calendar.YEAR);
    private final static int localMonth = localCal.get(Calendar.MONTH)+1;
    private final static int localDay = localCal.get(Calendar.DAY_OF_MONTH);
    private final static int localHour = localCal.get(Calendar.HOUR_OF_DAY);
    private final static int localMinute = localCal.get(Calendar.MINUTE);
    private final static int localSecond = localCal.get(Calendar.SECOND);
    private final static int localMs =localCal.get(Calendar.MILLISECOND);

    private String zeropad(int value, int digits)
    {
        char buf[] = new char[digits];
        int index = digits-1;
        while (index >= 0)
        {
            buf[index--] = (char) ('0'+ value % 10);
            value /= 10;
        }
        return new String(buf);
    }

    private String makeDateString(int year, int month, int day)
    {
        StringBuilder result = new StringBuilder();
        result.append(zeropad(year,4)).append(zeropad(month,2))
              .append(zeropad(day,2));
        return result.toString();
    }

    private String makeTimeString(int hour, int minute, int second, int ms)
    {
        StringBuilder result = new StringBuilder();
        result.append(zeropad(hour,2)).append(':')
              .append(zeropad(minute,2)).append(':')
              .append(zeropad(second,2)).append('.')
              .append(zeropad(ms,3));
        return result.toString();
    }

    private String makeDateTimeString(int year, int month, int day,
                                      int hour, int minute, int second, int ms)
    {
        return makeDateString(year, month, day) + "-"
             + makeTimeString(hour, minute, second, ms);
    }

    // If the end of the current second is near, wait until it changes so that
    // when a test makes two successive calls to get current time, those calls
    // will return values within the same second.
    private void avoidSecondTick()
    {
        while ((System.currentTimeMillis()%1000) > 950)
        {
            try { Thread.sleep(10); }   // 10 millisecond wait
            catch (InterruptedException e) { /**/ }
        }
    }

    // Wait until the system timer ticks over to the next millisecond so that
    // when a test makes two successive calls to get current time, those calls
    // will return values within the same millisecond.
    private void avoidMillisecondTick()
    {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() == start)
        {
            try { Thread.sleep(0, 50000); } // 50 microsecond wait
            catch (InterruptedException e) { /**/ }
        }
    }

    @Test public void testFloorDivide()
    {
        assertEquals(-3, DateHelper.floorDivide(-11, 5));
        assertEquals(-2, DateHelper.floorDivide(-10, 5));
        assertEquals(-2, DateHelper.floorDivide(-9, 5));
        assertEquals(-2, DateHelper.floorDivide(-6, 5));
        assertEquals(-1, DateHelper.floorDivide(-5, 5));
        assertEquals(-1, DateHelper.floorDivide(-4, 5));
        assertEquals(-1, DateHelper.floorDivide(-1, 5));
        assertEquals(0, DateHelper.floorDivide(0, 5));
        assertEquals(0, DateHelper.floorDivide(1, 5));
        assertEquals(0, DateHelper.floorDivide(4, 5));
        assertEquals(1, DateHelper.floorDivide(5, 5));
        assertEquals(1, DateHelper.floorDivide(6, 5));
        assertEquals(1, DateHelper.floorDivide(9, 5));
        assertEquals(2, DateHelper.floorDivide(10, 5));
        assertEquals(2, DateHelper.floorDivide(11, 5));

        assertEquals(2, DateHelper.floorDivide(-11, -5));
        assertEquals(2, DateHelper.floorDivide(-10, -5));
        assertEquals(1, DateHelper.floorDivide(-9, -5));
        assertEquals(1, DateHelper.floorDivide(-6, -5));
        assertEquals(1, DateHelper.floorDivide(-5, -5));
        assertEquals(0, DateHelper.floorDivide(-4, -5));
        assertEquals(0, DateHelper.floorDivide(-1, -5));
        assertEquals(0, DateHelper.floorDivide(0, -5));
        assertEquals(-1, DateHelper.floorDivide(1, -5));
        assertEquals(-1, DateHelper.floorDivide(4, -5));
        assertEquals(-1, DateHelper.floorDivide(5, -5));
        assertEquals(-2, DateHelper.floorDivide(6, -5));
        assertEquals(-2, DateHelper.floorDivide(9, -5));
        assertEquals(-2, DateHelper.floorDivide(10, -5));
        assertEquals(-3, DateHelper.floorDivide(11, -5));
    }

    @Test public void testConvertDaysToMilliseconds()
    {
        final int msPerDay = 24*60*60*1000;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i*msPerDay, DateHelper.convertDaysToMilliseconds(i));
        }
    }

    @Test public void testConvertHoursToMilliseconds()
    {
        final int msPerHour = 60*60*1000;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i*msPerHour, DateHelper.convertHoursToMilliseconds(i));
        }
    }

    @Test public void testConvertHoursToSeconds()
    {
        final int secPerHour = 60*60;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i*secPerHour, DateHelper.convertHoursToSeconds(i));
        }
    }

    @Test public void testConvertMinutesToMilliseconds()
    {
        final int msPerMin = 60*1000;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i*msPerMin, DateHelper.convertMinutesToMilliseconds(i));
        }
    }

    @Test public void testConvertMinutesToSeconds()
    {
        final int secPerMin = 60;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i*secPerMin, DateHelper.convertMinutesToSeconds(i));
        }
    }

    @Test public void testConvertSecondsToHours()
    {
        final int secPerHour = 60*60;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i, DateHelper.convertSecondsToHours(i*secPerHour));
        }
    }

    @Test public void testConvertSecondsToMinutes()
    {
        final int secPerMin = 60;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i, DateHelper.convertSecondsToMinutes(i*secPerMin+3));
        }
    }

    @Test public void testConvertSecondsToMilliseconds()
    {
        final int msPerSec = 1000;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i*msPerSec, DateHelper.convertSecondsToMilliseconds(i));
        }
    }

    @Test public void testConvertMillisecondsToSeconds()
    {
        final int msPerSec = 1000;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i, DateHelper.convertMillisecondsToSeconds(i*msPerSec+8));
        }
    }

    @Test public void testConvertMillisecondsToMinutes()
    {
        final int msPerMin = 60*1000;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i, DateHelper.convertMillisecondsToMinutes(i*msPerMin+42));
        }
    }

    @Test public void testConvertMillisecondsToHours()
    {
        final int msPerHour = 60*60*1000;
        for (int i = 1; i < 5; ++i)
        {
            assertEquals("i=" + i,
                    i, DateHelper.convertMillisecondsToHours(i*msPerHour+123));
        }
    }

    @Test public void testGetMillisecondOfDay()
    {
        final int msPerSec = 1000;
        int msTimes[] = { 123489753, 308972345, 273483934, 63859302 };
        for (int time : msTimes)
        {
            assertEquals("time=" + time,
                    time % msPerSec, DateHelper.getMillisecondOfDay(time));
        }
    }

    @Test public void testGetSecondOfDay()
    {
        final int msPerSec = 1000;
        final int secPerMin = 60;
        int msTimes[] = { 123489753, 308972345, 273483934, 63859302 };
        for (int time : msTimes)
        {
            assertEquals("time=" + time,
                    (time / msPerSec) % secPerMin,
                    DateHelper.getSecondOfDay(time));
        }
    }

    @Test public void testGetMinuteOfDay()
    {
        final int msPerMin = 60*1000;
        final int minPerHour = 60;
        int msTimes[] = { 123489753, 308972345, 273483934, 63859302 };
        for (int time : msTimes)
        {
            assertEquals("time=" + time,
                    (time / msPerMin) % minPerHour,
                    DateHelper.getMinuteOfDay(time));
        }
    }

    @Test public void testGetHourOfDay()
    {
        assertEquals(localHour, DateHelper.getHourOfDay(msNow));
        assertEquals(utcHour, DateHelper.getHourOfDay(msNow, utcOffset));
    }

    @Test public void testGets()
    {
        // Adapted from earlier test class
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, localYear);
        cal.set(Calendar.MONTH, localMonth-1);
        cal.set(Calendar.DAY_OF_MONTH, localDay);
        cal.set(Calendar.MILLISECOND, 789);

        for (int hh = 0; hh < 24; hh++)
        {
            for (int mm = 0; mm < 60; mm++)
            {
                for (int ss = 0; ss < 60; ss++)
                {
                    String id = hh + ":" + mm + ":" + ss;
                    cal.set(Calendar.HOUR_OF_DAY, hh);
                    cal.set(Calendar.MINUTE, mm);
                    cal.set(Calendar.SECOND, ss);
                    long calTime = cal.getTime().getTime();
                    assertEquals(id, hh, DateHelper.getHourOfDay(calTime));
                    assertEquals(id, mm, DateHelper.getMinuteOfDay(calTime));
                    assertEquals(id, ss, DateHelper.getSecondOfDay(calTime));
                }
            }
        }
    }

    @Test public void testCurrentTimeInSeconds()
    {
        final int msPerSec = 1000;
        final long secNow = msNow / msPerSec;
        assertTrue(DateHelper.currentTimeInSeconds() - secNow <= 1);
    }

    @Test public void testIsLeapYear()
    {
        assertFalse(DateHelper.isLeapYear(1900));
        assertTrue(DateHelper.isLeapYear(2000));
        assertFalse(DateHelper.isLeapYear(2100));
        assertTrue(DateHelper.isLeapYear(1984));
        assertFalse(DateHelper.isLeapYear(1986));
    }

    @Test public void testEquals()
    {
        DateStruct date1 = new DateStruct((byte)6, (byte)17, (short)1972);
        DateStruct date2 = new DateStruct((byte)7, (byte)20, (short)1969);
        DateStruct date3 = new DateStruct((byte)6, (byte)17, (short)1972);
        assertTrue(DateHelper.equals(date1, date1));
        assertTrue(DateHelper.equals(date1, date3));
        assertFalse(DateHelper.equals(date1, date2));

        TimeStruct time1 = new TimeStruct((byte)4, (byte)0, (byte)0, (byte)0);
        TimeStruct time2 = new TimeStruct((byte)4, (byte)0, (byte)0, (byte)10);
        TimeStruct time3 = new TimeStruct((byte)4, (byte)0, (byte)0, (byte)0);
        TimeStruct time4 = new TimeStruct((byte)11, (byte)8, (byte)23,(byte)42);
        assertFalse(DateHelper.equals(time1, time2));
        assertTrue(DateHelper.equalsHHMMSS(time1, time2));
        assertTrue(DateHelper.equals(time1, time3));
        assertTrue(DateHelper.equalsHHMMSS(time1, time3));
        assertFalse(DateHelper.equals(time1, time4));
        assertFalse(DateHelper.equalsHHMMSS(time1, time4));
    }

    @Test public void testDateStructs()
    {
        // Adapted from earlier test class
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        DateStruct cachedDateStruct = new DateStruct();
        for (int i = 0; i < (100 * DateHelper.DAYS_PER_YEAR); i++)
        {
            String id = "i=" + i;
            cachedDateStruct.year  = (short) cal.get(Calendar.YEAR);
            cachedDateStruct.month = (byte) (cal.get(Calendar.MONTH) + 1);
            cachedDateStruct.day   = (byte)  cal.get(Calendar.DATE);
            long ms = cal.getTime().getTime();

            DateStruct newDateStruct = DateHelper.convertDateToDateStruct(
                    ms, DateHelper.TIMEZONE_OFFSET_UTC, new DateStruct());
            assertEquals(id, cachedDateStruct.year, newDateStruct.year);
            assertEquals(id, cachedDateStruct.month, newDateStruct.month);
            assertEquals(id, cachedDateStruct.day, newDateStruct.day);

            Date fromCache =
                    DateHelper.convertDateStructToDate(cachedDateStruct);
            Date fromNew = DateHelper.convertDateStructToDate(newDateStruct);
            assertEquals(id, fromCache.getYear(), fromNew.getYear());
            assertEquals(id, fromCache.getMonth(), fromNew.getMonth());
            assertEquals(id, fromCache.getDate(), fromNew.getDate());
            assertEquals(id, fromCache.getHours(), fromNew.getHours());
            assertEquals(id, fromCache.getMinutes(), fromNew.getMinutes());
            assertEquals(id, fromCache.getSeconds(), fromNew.getSeconds());

            cal.add(Calendar.DATE, 1);
        }
    }

    @Test public void testMakeYYYY() throws Exception
    {
        int years[] = { 1789, 2001, 7510 };
        char buf[] = new char[10];
        for (int year : years)
        {
            String yearString = Integer.toString(year);

            DateHelper.makeYYYY(buf, year);
            String s = String.copyValueOf(buf, 0, 4);
            assertEquals(yearString, s);

            StringWriter sw = new StringWriter();
            DateHelper.makeYYYY(sw, year);
            assertEquals(yearString, sw.toString());

            FastCharacterWriter fcw = new FastCharacterWriter();
            DateHelper.makeYYYY(fcw, year);
            assertEquals(yearString, fcw.toString());
        }
    }

    @Test public void testMakeYYYYMMDD() throws Exception
    {
        int year = 1066;
        int month = 9;
        int day = 28;
        String dateString = "10660928";

        char buf[] = new char[10];
        DateHelper.makeYYYYMMDD(buf, year, month, day);
        assertEquals(dateString, String.copyValueOf(buf, 0, 8));

        StringWriter sw = new StringWriter();
        DateHelper.makeYYYYMMDD(sw, year, month, day);
        assertEquals(dateString, sw.toString());

        FastCharacterWriter fcw = new FastCharacterWriter();
        DateHelper.makeYYYYMMDD(fcw, year, month, day);
        assertEquals(dateString, fcw.toString());

        year = 1066;
        month = 10;
        day = 14;
        DateStruct ds = new DateStruct((byte)10, (byte)14, (short)1066);
        dateString = "10661014";
        DateHelper.makeYYYYMMDD(buf, ds);
        assertEquals(dateString, String.copyValueOf(buf, 0, 8));

        sw = new StringWriter();
        DateHelper.makeYYYYMMDD(sw, ds);
        assertEquals(dateString, sw.toString());

        fcw = new FastCharacterWriter();
        DateHelper.makeYYYYMMDD(fcw, ds);
        assertEquals(dateString, fcw.toString());

        DateHelper.makeYYYYMMDD(buf, 1, year, month, day);
        assertEquals("1"+dateString, String.copyValueOf(buf, 0, 9));

        buf[0] = '2';
        DateHelper.makeYYYYMMDD(buf, 1, ds);
        assertEquals("2"+dateString, String.copyValueOf(buf, 0, 9));
    }

    @Test public void testMakeHHMMSSsss() throws Exception
    {
        String localString = makeTimeString(
                localHour, localMinute, localSecond, localMs);
        String utcString = makeTimeString(utcHour, utcMinute, utcSecond, utcMs);

        char buf[] = new char[15];
        DateHelper.makeHHMMSSsss(buf, 0, msNow, localOffset);
        assertEquals(localString, String.copyValueOf(buf, 0, 12));

        DateHelper.makeHHMMSSsss(buf, 0, msNow, utcOffset);
        assertEquals(utcString, String.copyValueOf(buf, 0, 12));

        DateHelper.makeHHMMSSsss(buf, 0, msNow);
        assertEquals(localString, String.copyValueOf(buf, 0, 12));

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(ba);

        DateHelper.makeHHMMSSsss(ps, 0L);
        assertEquals(EMPTY_TIME, ba.toString());

        ba = new ByteArrayOutputStream();
        ps = new PrintStream(ba);
        DateHelper.makeHHMMSSsss(ps, msNow);
        assertEquals(localString, ba.toString());

        assertEquals(EMPTY_TIME, new String(DateHelper.makeHHMMSSsss(0L)));
        assertEquals(localString, new String(DateHelper.makeHHMMSSsss(msNow)));

        assertEquals(EMPTY_TIME, new String(
                DateHelper.makeHHMMSSsss(0L, utcOffset)) );
        assertEquals(utcString, new String(
                DateHelper.makeHHMMSSsss(msNow, utcOffset)) );

        StringWriter sw = new StringWriter();
        DateHelper.makeHHMMSSsss(sw, 0L, localOffset);
        assertEquals(EMPTY_TIME, sw.toString());
        sw = new StringWriter();
        DateHelper.makeHHMMSSsss(sw, msNow, localOffset);
        assertEquals(localString, sw.toString());

        FastCharacterWriter fcw = new FastCharacterWriter();
        DateHelper.makeHHMMSSsss(fcw, 0L, utcOffset);
        assertEquals(EMPTY_TIME, fcw.toString());
        fcw = new FastCharacterWriter();
        DateHelper.makeHHMMSSsss(fcw, msNow, utcOffset);
        assertEquals(utcString, fcw.toString());

        fcw = new FastCharacterWriter();
        DateHelper.makeHHMMSSsss(fcw, localHour, localMinute, localSecond,
                localMs);
        assertEquals(localString, fcw.toString());
    }

    @Test public void testMakeHHMMSS() throws Exception
    {
        final long someTime = 1282238102123L; // 2010-8-19 12:15:02.123 CDT
        String cdtString = "12:15:02";

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(ba);

        DateHelper.makeHHMMSS(ps, 0L);
        assertEquals(EMPTY_TIME, ba.toString());

        ba = new ByteArrayOutputStream();
        ps = new PrintStream(ba);
        DateHelper.makeHHMMSS(ps, someTime);
        assertEquals(cdtString, ba.toString());

        byte hour = 12;
        byte minute = 15;
        byte second = 2;

        char buf[] = new char[15];
        DateHelper.makeHHMMSS(buf, hour, minute, second);
        assertEquals(cdtString, new String(buf, 0, 8));

        StringWriter sw = new StringWriter();
        DateHelper.makeHHMMSS(sw, hour, minute, second);
        assertEquals(cdtString, sw.toString());

        FastCharacterWriter fcw = new FastCharacterWriter();
        DateHelper.makeHHMMSS(fcw, hour, minute, second);
        assertEquals(cdtString, fcw.toString());

        byte millisecond = 0;
        TimeStruct localTs = new TimeStruct(hour, minute, second, millisecond);
        DateHelper.makeHHMMSS(buf, localTs);
        assertEquals(cdtString, String.valueOf(buf, 0, 8));

        sw = new StringWriter();
        DateHelper.makeHHMMSS(sw, localTs);
        assertEquals(cdtString, sw.toString());

        fcw = new FastCharacterWriter();
        DateHelper.makeHHMMSS(fcw, localTs);
        assertEquals(cdtString, fcw.toString());

        Arrays.fill(buf, '\0');
        DateHelper.makeHHMMSS(buf, 0, hour, minute, second);
        assertEquals(cdtString, String.valueOf(buf, 0, 8));

        DateHelper.makeHHMMSS(buf, 1, localTs);
        assertEquals(cdtString.charAt(0)+cdtString, String.valueOf(buf, 0, 9));
    }

    @Test public void testValidateYYYYMMDD()
    {
        assertFalse(DateHelper.validateYYYYMMDD(0, 0, 0));
        assertFalse(DateHelper.validateYYYYMMDD(1066, 9, 28));
        assertFalse(DateHelper.validateYYYYMMDD(5000, 10, 4));
        assertTrue(DateHelper.validateYYYYMMDD(1970, 1, 1));
        assertTrue(DateHelper.validateYYYYMMDD(1970, 2, 28));
        assertFalse(DateHelper.validateYYYYMMDD(1970,2, 29));
        assertTrue(DateHelper.validateYYYYMMDD(1972, 2, 29));
        assertTrue(DateHelper.validateYYYYMMDD(2000, 12, 31));
        assertFalse(DateHelper.validateYYYYMMDD(2000, 13, 1));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 1, 31));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 2, 28));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 3, 31));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 4, 30));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 5, 31));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 6, 30));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 7, 31));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 8, 31));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 9, 30));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 10, 31));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 11, 30));
        assertTrue(DateHelper.validateYYYYMMDD(2001, 12, 31));

        assertFalse(DateHelper.validateYYYYMMDD(2001, 1, 32));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 2, 29));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 3, 32));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 4, 31));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 5, 32));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 6, 31));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 7, 32));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 8, 32));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 9, 31));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 10, 32));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 11, 31));
        assertFalse(DateHelper.validateYYYYMMDD(2001, 12, 32));
    }

    @Test public void testConvertDateToThreadLocalDateStruct()
    {
        DateStruct d =
                DateHelper.convertDateToThreadLocalDateStruct(msNow, utcOffset);
        assertEquals(utcYear, d.year);
        assertEquals(utcMonth, d.month);
        assertEquals(utcDay, d.day);

        d = DateHelper.convertDateToThreadLocalDateStruct(msNow, localOffset);
        assertEquals(localYear, d.year);
        assertEquals(localMonth, d.month);
        assertEquals(localDay, d.day);

        d = DateHelper.convertDateToThreadLocalDateStruct(msNow);
        assertEquals(localYear, d.year);
        assertEquals(localMonth, d.month);
        assertEquals(localDay, d.day);
    }

    @Test public void testConvertDateToThreadLocalTimeStruct()
    {
        TimeStruct t =
                DateHelper.convertDateToThreadLocalTimeStruct(msNow, utcOffset);
        assertEquals(utcHour, t.hour);
        assertEquals(utcMinute, t.minute);
        assertEquals(utcSecond, t.second);
        assertEquals(0, t.fraction);    // method does not set this field

        t = DateHelper.convertDateToThreadLocalTimeStruct(msNow);
        assertEquals(localHour, t.hour);
        assertEquals(localMinute, t.minute);
        assertEquals(localSecond, t.second);
        assertEquals(0, t.fraction);    // method does not set this field
    }

    @Test public void testConvertDateToDateStruct()
    {
        Date date = new Date(utcYear-1900, utcMonth-1, utcDay);
        DateStruct ds;
        ds = DateHelper.convertDateToDateStruct(date, new DateStruct());
        assertEquals(utcYear, ds.year);
        assertEquals(utcMonth, ds.month);
        assertEquals(utcDay, ds.day);

        ds = DateHelper.convertDateToDateStruct(date, localOffset,
                new DateStruct());
        assertEquals(localYear, ds.year);
        assertEquals(localMonth, ds.month);
        assertEquals(localDay, ds.day);

        short years[] = { 1970, 1972, 2000, 2100, 2524, 2525, 3000 };
        for (int y : years)
        {
            // March 1, after leap day if the year has one
            date = new Date(y - 1900, 2, 1);
            DateHelper.convertDateToDateStruct(date, utcOffset, ds);
            assertEquals(y, ds.year);
            assertEquals(3, ds.month);
            assertEquals(1, ds.day);
        }

        ds = DateHelper.convertDateToDateStruct(msNow, new DateStruct());
        assertEquals(utcYear, ds.year);
        assertEquals(utcMonth, ds.month);
        assertEquals(utcDay, ds.day);

        ds = DateHelper.convertDateToDateStruct(msNow, localOffset,
                new DateStruct());
        assertEquals(localYear, ds.year);
        assertEquals(localMonth, ds.month);
        assertEquals(localDay, ds.day);

        ds = DateHelper.convertDateToDateStruct(msNow, utcOffset);
        assertEquals(utcYear, ds.year);
        assertEquals(utcMonth, ds.month);
        assertEquals(utcDay, ds.day);
    }

    @Test public void testConvertDateToTimeStruct()
    {
        TimeStruct ts = new TimeStruct();
        ts = DateHelper.convertDateToTimeStruct(msNow, ts);
        assertEquals(localHour, ts.hour);
        assertEquals(localMinute, ts.minute);
        assertEquals(localSecond, ts.second);

        ts = DateHelper.convertDateToTimeStruct(msNow, utcOffset, ts);
        assertEquals(utcHour, ts.hour);
        assertEquals(utcMinute, ts.minute);
        assertEquals(utcSecond, ts.second);
    }

    @Test public void testConvertDateStructToDate()
    {
        Date date;
        date = DateHelper.convertDateStructToDate(null);
        assertNull(date);
        DateStruct ds = new DateStruct((byte)utcMonth, (byte)utcDay, (short)utcYear);
        date = DateHelper.convertDateStructToDate(ds);
        assertEquals(utcYear-1900, date.getYear());
        assertEquals(utcMonth-1, date.getMonth());
        assertEquals(utcDay, date.getDate());

        date = DateHelper.convertDateStructToDate(ds, localOffset);
        assertEquals(localYear-1900, date.getYear());
        assertEquals(localMonth-1, date.getMonth());
        assertEquals(localDay, date.getDate());
    }

    @Test public void testConvertMaturityToDateStruct()
    {
        DateStruct ds = DateHelper.convertMaturityToDateStruct(
                localYear*100+localMonth, localDay);
        assertEquals(localYear, ds.year);
        assertEquals(localMonth, ds.month);
        assertEquals(localDay, ds.day);
    }

    @Test public void testExtractDateStruct()
    {
        String badDates[] = { "17890714", "30050102", "20x00510", "199z0623",
                "20012020", "20011703", "20010a03", "19990641", "1998062y",
                "20010229" };
        // char array yyyymmdd
        DateStruct ds;

        for (String s : badDates)
        {
            ds = DateHelper.extractDateStruct(s.toCharArray(), 0, 8);
            assertNull(s, ds);
            ds = DateHelper.extractDateStruct(s);
            assertNull(s, ds);
        }

        ds = DateHelper.extractDateStruct("19700101".toCharArray(), 0, 8);
        assertEquals(1970, ds.year);
        assertEquals(1, ds.month);
        assertEquals(1, ds.day);
        ds = DateHelper.extractDateStruct("19700101");
        assertEquals(1970, ds.year);
        assertEquals(1, ds.month);
        assertEquals(1, ds.day);

        ds = DateHelper.extractDateStruct("019710101".toCharArray(), 0, 9);
        assertNull(ds);
        ds = DateHelper.extractDateStruct("019710101");
        assertNull(ds);

        ds = DateHelper.extractDateStruct("x19700101".toCharArray(), 1, 8);
        assertEquals(1970, ds.year);
        assertEquals(1, ds.month);
        assertEquals(1, ds.day);
    }

    @Test public void testStringizeDate()
    {
        Date date = new Date(localYear-1900, localMonth-1, localDay);
        String s = DateHelper.stringizeDate(null, date);
        assertNull(s);

        s = DateHelper.stringizeDate(
                DateHelper.static_fixLocalMktDateFormat, date);
        String localString = makeDateString(localYear, localMonth, localDay);
        assertEquals(localString, s);
    }

    @Test public void testExtractDate()
    {
        String localString = makeDateString(localYear, localMonth, localDay);
        Date date = DateHelper.extractDate(null, localString);
        assertNull(date);

        date = DateHelper.extractDate(
                DateHelper.static_fixLocalMktDateFormat, localString);
        assertEquals(localYear-1900, date.getYear());
        assertEquals(localMonth-1, date.getMonth());
        assertEquals(localDay, date.getDate());
    }

    @Test public void testDateInLogFormat()
    {
        String s = DateHelper.stringizeDateInLogFormat(msNow);
        String localString = makeDateTimeString(localYear, localMonth, localDay,
                localHour, localMinute, localSecond, localMs);
        assertEquals(localString, s);

        avoidMillisecondTick();
        long now = System.currentTimeMillis();
        s = DateHelper.stringizeDateInLogFormat();
        Calendar cal = new GregorianCalendar(localZone);
        cal.setTimeInMillis(now);
        localString = makeDateTimeString(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND));
        assertEquals(localString, s);

        Date date = new Date(now);
        s = DateHelper.stringizeDateInLogFormat(date);
        assertEquals(localString, s);

        FastCharacterWriter fcw = new FastCharacterWriter();
        DateHelper.appendDateInLogFormat(fcw, now);
        assertEquals(localString, fcw.toString());
    }

    @Test public void testExtractDateInFixUTCDateFormat()
    {
        String dateString = "19720617";
        Date date = DateHelper.extractDateInFixUTCDateFormat(
                dateString.toCharArray(), 0, 8);
        assertEquals(72, date.getYear());
        assertEquals(5, date.getMonth());
        // We constructed date from year/month/day so hour/minute/second are 0.
        // If local time zone is earlier than UTC, using it for adjustment will
        // move us to before midnight.
        int dayOffset = localOffset < 0 ? -1 : 0;
        assertEquals(17+dayOffset, date.getDate());

        date = DateHelper.extractDateInFixUTCDateFormat(dateString);
        assertEquals(72, date.getYear());
        assertEquals(5, date.getMonth());
        assertEquals(17+dayOffset, date.getDate());
    }

    @Test public void testStringizeDateInFixUTCDateFormat()
    {
        long now = System.currentTimeMillis();
        String s = DateHelper.stringizeDateInFixUTCDateFormat();
        Calendar cal = new GregorianCalendar(utcZone);
        cal.setTimeInMillis(now);
        String utcString = makeDateString(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1,
                cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(utcString, s);

        Date date = new Date(now);
        s = DateHelper.stringizeDateInFixUTCDateFormat(date);
        assertEquals(utcString, s);

        s = DateHelper.stringizeDateInFixUTCDateFormat(now);
        assertEquals(utcString, s);
    }

    @Test public void testAppendDateInFixUTCDateFormat() throws Exception
    {
        DateStruct d = new DateStruct(
                (byte)utcMonth, (byte)utcDay, (short)utcYear);
        String utcString = makeDateString(utcYear, utcMonth, utcDay);

        FastCharacterWriter fcw = new FastCharacterWriter();
        DateHelper.appendDateInFixUTCDateFormat(fcw, d);
        assertEquals(utcString, fcw.toString());

        StringWriter sw = new StringWriter();
        DateHelper.appendDateInFixUTCDateFormat(sw, d);
        assertEquals(utcString, sw.toString());

        fcw = new FastCharacterWriter();
        DateHelper.appendDateInFixUTCDateFormat(fcw, msNow);
        assertEquals(utcString, fcw.toString());

        sw = new StringWriter();
        DateHelper.appendDateInFixUTCDateFormat(sw, msNow);
        assertEquals(utcString, sw.toString());
    }

    @Test public void testExtractDateInFixUTCTimeOnlyFormat()
    {
        // (todo) note adjustment due to daylight savings time
        // It seems that the Date is read as local Standard time.
        int daylightAdjustment = localZone.inDaylightTime(new Date()) ? 1 : 0;

        String utcString = makeTimeString(utcHour, utcMinute, utcSecond, utcMs);
        Date date = DateHelper.extractDateInFixUTCTimeOnlyFormat(
                utcString.toCharArray(), 0, 8);
        assertEquals("daylightAdjustment:" + daylightAdjustment,
                localHour, date.getHours()+daylightAdjustment);
        assertEquals(localMinute, date.getMinutes());
        assertEquals(localSecond, date.getSeconds());

        utcString = utcString.substring(0, 8);  // remove milliseconds
        date = DateHelper.extractDateInFixUTCTimeOnlyFormat(utcString);
        assertEquals("daylightAdjustment:" + daylightAdjustment,
                localHour, date.getHours()+daylightAdjustment);
        assertEquals(localMinute, date.getMinutes());
        assertEquals(localSecond, date.getSeconds());
    }

    @Test public void testStringizeDateInFixUTCTimeOnlyFormat()
    {
        // We call System.currentTimeMillis here and also in the no-parameters
        // method DateHelper.stringizeDateInFixUTCTimeOnlyFormat. Ensure that
        // the 2 calls return times in the same second so that the test (which
        // only compares times to the second, not millisecond) doesn't report
        // failure just because the clock ticked to the next second between the
        // 2 calls.
        avoidSecondTick();

        long now = System.currentTimeMillis();
        String s = DateHelper.stringizeDateInFixUTCTimeOnlyFormat();
        Calendar cal = new GregorianCalendar(utcZone);
        cal.setTimeInMillis(now);
        String utcString = makeTimeString(cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND))
              .substring(0, 8); // don't need millisecond for this test
        assertEquals(utcString, s);

        s = DateHelper.stringizeDateInFixUTCTimeOnlyFormat(new Date(now));
        assertEquals(utcString, s);

        s = DateHelper.stringizeDateInFixUTCTimeOnlyFormat(now);
        assertEquals(utcString, s);

        TimeStruct time = new TimeStruct((byte)-1, (byte)0, (byte)0, (byte)0);
        s = DateHelper.stringizeDateInFixUTCTimeOnlyFormat(time);
        assertEquals(EMPTY_TIME, s);

        utcString = makeTimeString(utcHour, utcMinute, utcSecond, 0)
                .substring(0, 8);
        time.hour   = (byte) utcHour;
        time.minute = (byte) utcMinute;
        time.second = (byte) utcSecond;
        s = DateHelper.stringizeDateInFixUTCTimeOnlyFormat(time);
        assertEquals(utcString, s);
    }

    @Test public void testAppendDateInFixUTCTimeOnlyFormat() throws Exception
    {
        String utcString = makeTimeString(utcHour, utcMinute, utcSecond, 0)
                .substring(0, 8);   // don't need millisecond for this test

        FastCharacterWriter fcw = new FastCharacterWriter();
        DateHelper.appendDateInFixUTCTimeOnlyFormat(fcw, msNow);
        assertEquals(utcString, fcw.toString());

        StringWriter sw = new StringWriter();
        DateHelper.appendDateInFixUTCTimeOnlyFormat(sw, msNow);
        assertEquals(utcString, sw.toString());

        TimeStruct time = new TimeStruct(
                (byte) utcHour, (byte) utcMinute, (byte) utcSecond, (byte)0);
        fcw = new FastCharacterWriter();
        DateHelper.appendDateInFixUTCTimeOnlyFormat(fcw, time);
        assertEquals(utcString, fcw.toString());

        sw = new StringWriter();
        DateHelper.appendDateInFixUTCTimeOnlyFormat(sw, time);
        assertEquals(utcString, sw.toString());
    }

    @Test public void testExtractDateInFixUTCTimeStampFormat()
    {
        // Seems to read time string in UTC but produce Date in local timezone.
        String utcString = makeDateTimeString(utcYear, utcMonth, utcDay,
                utcHour, utcMinute, utcSecond, utcMs);
        Date date = DateHelper.extractDateInFixUTCTimeStampFormat(
                utcString.toCharArray(), 0, 17);
        assertEquals(localYear-1900, date.getYear());
        assertEquals(localMonth-1, date.getMonth());
        assertEquals(localDay, date.getDate());
        assertEquals(localHour, date.getHours());
        assertEquals(localMinute, date.getMinutes());
        assertEquals(localSecond, date.getSeconds());

        date = DateHelper.extractDateInFixUTCTimeStampFormat(utcString);
        assertEquals(localYear-1900, date.getYear());
        assertEquals(localMonth-1, date.getMonth());
        assertEquals(localDay, date.getDate());
        assertEquals(localHour, date.getHours());
        assertEquals(localMinute, date.getMinutes());
        assertEquals(localSecond, date.getSeconds());
    }

    @Test public void testStringizeDateInFixUTCTimeStampFormat()
    {
        String utcString = makeDateTimeString(utcYear, utcMonth, utcDay,
                utcHour, utcMinute, utcSecond, utcMs)
                .substring(0, 17);  // don't want milliseconds for this test
        String s = DateHelper.stringizeDateInFixUTCTimeStampFormat(msNow);
        assertEquals(utcString, s);

        avoidMillisecondTick();
        long now = System.currentTimeMillis();
        s = DateHelper.stringizeDateInFixUTCTimeStampFormat();
        Calendar cal = new GregorianCalendar(utcZone);
        cal.setTimeInMillis(now);
        utcString = makeDateTimeString(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND))
                .substring(0, 17);  // don't want milliseconds for this test
        assertEquals(utcString, s);
    }

    @Test public void testAppendDateInFixUTCTimeStampFormat() throws Exception
    {
        String utcString = makeDateTimeString(utcYear, utcMonth, utcDay,
                utcHour, utcMinute, utcSecond, utcMs)
                .substring(0, 17);  // don't want milliseconds for this test
        DateStruct date =
                new DateStruct((byte)utcMonth, (byte)utcDay, (short) utcYear);
        TimeStruct time = new TimeStruct(
                (byte)utcHour, (byte)utcMinute, (byte)utcSecond, (byte)utcMs);

        FastCharacterWriter fcw = new FastCharacterWriter();
        DateHelper.appendDateInFixUTCTimeStampFormat(fcw, date, time);
        assertEquals(utcString, fcw.toString());

        StringWriter sw = new StringWriter();
        DateHelper.appendDateInFixUTCTimeStampFormat(sw, date, time);
        assertEquals(utcString, sw.toString());

        fcw = new FastCharacterWriter();
        DateHelper.appendDateInFixUTCTimeStampFormat(fcw, msNow);
        assertEquals(utcString, fcw.toString());

        sw = new StringWriter();
        DateHelper.appendDateInFixUTCTimeStampFormat(sw, msNow);
        assertEquals(utcString, sw.toString());
    }

    @Test public void testExtractDateInFixLocalMktDateFormat()
    {
        Date date = DateHelper.extractDateInFixLocalMktDateFormat(null, 0, 0);
        assertNull(date);

        String utcString = makeDateString(utcYear, utcMonth, utcDay);
        char utcChars[] = utcString.toCharArray();

        date = DateHelper.extractDateInFixLocalMktDateFormat(utcChars, 0, 8);
        assertEquals(utcYear-1900, date.getYear());
        assertEquals(utcMonth-1, date.getMonth());
        assertEquals(utcDay, date.getDate());

        date = DateHelper.extractDateInFixLocalMktDateFormat(utcString);
        assertEquals(utcYear-1900, date.getYear());
        assertEquals(utcMonth-1, date.getMonth());
        assertEquals(utcDay, date.getDate());
    }

    @Test public void testStringizeDateInFixLocalMktDateFormat()
    {
        long now = System.currentTimeMillis();
        String s = DateHelper.stringizeDateInFixLocalMktDateFormat();
        Calendar cal = new GregorianCalendar(utcZone);
        cal.setTimeInMillis(now);
        String utcString = makeDateString(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1,
                cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(utcString, s);

        Date date = null;
        s = DateHelper.stringizeDateInFixLocalMktDateFormat(date);
        assertEquals(EMPTY_DATE, s);

        date = new Date(now);
        s = DateHelper.stringizeDateInFixLocalMktDateFormat(date);
        assertEquals(utcString, s);

        s = DateHelper.stringizeDateInFixLocalMktDateFormat(now);
        assertEquals(utcString, s);
    }

    @Test public void testAppendDateInFixLocalMktDateFormat() throws Exception
    {
        DateStruct d =
                new DateStruct((byte)utcMonth, (byte)utcDay, (short)utcYear);
        String utcString = makeDateString(utcYear, utcMonth, utcDay);

        FastCharacterWriter fcw = new FastCharacterWriter();
        DateHelper.appendDateInFixLocalMktDateFormat(fcw, d);
        assertEquals(utcString, fcw.toString());

        StringWriter sw = new StringWriter();
        DateHelper.appendDateInFixLocalMktDateFormat(sw, d);
        assertEquals(utcString, sw.toString());

        String localString = makeDateString(localYear, localMonth, localDay);
        fcw = new FastCharacterWriter();
        DateHelper.appendDateInFixLocalMktDateFormat(fcw, msNow);
        assertEquals(localString, fcw.toString());

        sw = new StringWriter();
        DateHelper.appendDateInFixLocalMktDateFormat(sw, msNow);
        assertEquals(localString, sw.toString());
    }
}
