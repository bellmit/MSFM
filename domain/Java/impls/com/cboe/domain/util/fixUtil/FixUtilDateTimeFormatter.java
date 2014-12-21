package com.cboe.domain.util.fixUtil;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
/**
 * This class the implemenation of the TpfFixAdapter for the Appia FIX Engine
 * <br><br>
 * Copyright © 1999 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 * @author Jim Northey
 *
 */
public class FixUtilDateTimeFormatter
{
    private static ThreadLocal<GregorianCalendar> utcCalendarRef = new ThreadLocal<GregorianCalendar>()
    {
        protected GregorianCalendar initialValue(){
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            return new GregorianCalendar(timeZone);
        }
    };
    private static ThreadLocal<GregorianCalendar> calendarRef = new ThreadLocal<GregorianCalendar>()
    {
        protected GregorianCalendar initialValue(){
            return new GregorianCalendar();
        }
    };
    private static ThreadLocal<SimpleDateFormat> simpleDateFormatRef = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue(){
            return new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        }
    };
    private static ThreadLocal<SimpleDateFormat> simpleDateFormatYmdRef = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue(){
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    /**
     * Return FIX Timestamp for the current date time
     * OK
     */
    public static String currentTimeUTC()
    {
        return getFixTime( System.currentTimeMillis() );
    }



    /**
     * Return FIX Timestamp for the current date time
     */
    public static String currentLocalTime()
    {
        GregorianCalendar cal = calendarRef.get();
        cal.setTimeInMillis(System.currentTimeMillis());
        return time(cal);
    }



    /**
     * Return a UTC time representation of the provided date.
     * @param date The date is in the current time zone
     * OK
     */
    public static String getFixTime(Date date)
    {
        GregorianCalendar cal = utcCalendarRef.get();
        cal.setTime(date);
        return time(cal);
    }

    public static String getFixTime(long millis)
    {
        GregorianCalendar cal = utcCalendarRef.get();
        cal.setTimeInMillis(millis);
        return time(cal);
    }


    /**
     * @return The current timezone representation of the UTC date provided as a parameter
     * @param utcTimeStamp a Fix formatted UTC time stamp
     */
    public static Date getLocalDate(String utcTimeStamp) throws ParseException
    {
	if(utcTimeStamp.indexOf('-') > 0)
	{
	  SimpleDateFormat dateTimeFormat = simpleDateFormatRef.get();
	  GregorianCalendar cal = utcCalendarRef.get();

	  dateTimeFormat.setCalendar(cal);

	  return dateTimeFormat.parse(utcTimeStamp);
	}
	else
	{
	  SimpleDateFormat dateFormat = simpleDateFormatYmdRef.get();

	  return dateFormat.parse(utcTimeStamp);
	}
    }



    /*
     * Creates a FIX Timestamp from the Calendar
     * OK
     */
    public static String time(Calendar calendar)
    {
	byte[] data = new byte[17];

	writeIntAsString(calendar.get(Calendar.YEAR), data, 0, 4);
	writeIntAsString(calendar.get(Calendar.MONTH) + 1, data, 4, 2);
	writeIntAsString(calendar.get(Calendar.DAY_OF_MONTH), data, 6, 2);
	data[8] = (byte)'-';
	writeIntAsString(calendar.get(Calendar.HOUR_OF_DAY), data, 9, 2);
	data[11] = (byte)':';
	writeIntAsString(calendar.get(Calendar.MINUTE), data, 12, 2);
	data[14] = (byte)':';
	writeIntAsString(calendar.get(Calendar.SECOND), data, 15, 2);

	return new String(data);
    }




    /**
     * Formats an integer year and month into the FIX MaturityMonthYear(200)
     * field format.
     *
     * @param year four digit year
     * @param month one or two digit month (1 = January)
     */
    public static String maturityMonthYear(int year, int month) {
      byte[] data = new byte[6];

      writeIntAsString(year, data, 0, 4);
      writeIntAsString(month, data, 4, 2);

      return new String(data);
    }



    /**
     * Copies a number into the specified buffer at the specified offset of
     * specified max length.
     * The byte[] is prefixed with 0's if the length is larger then the value
     * of the number.
     *
     * @author Ravi Vazirani
     *
     * @param data byte[] array that holds the output integer.
     * @param offset int where the string will be stored in the bave buffer.
     * @param length int length of the field in the output buffer
     * @param value int to be copied in the output buffer.
     */
    public static void writeIntAsString(int value, byte[] data, int offset, int length)
    {

	    for (int i = length - 1; i >= 0; i--, value /= 10)
	    {
		    data[offset+i] = (byte)((value == 0) ? '0' : (byte)((value % 10) + '0'));
	    }
    }

    /**
     * Converts a numeric value from character-string format to a integer.
     * Ignores spaces and nulls so be carefull.
     * NULLS AND SPACES MAY BE ONLY LEADING OR TRALING CHARS IN THE BYTES.
     *
     * @author Ravi Vazirani
     *
     * @return int
     * @param data byte[]
     * @param offset int
     * @param length int
     */
    public static int readStringAsInt(byte[] data, int offset, int length)
    {
	    int value, returnValue = 0;

	    for (int i = 0; i < length; i++)
	    {
		    value = data[offset+i];
		    if (value >= '0' && value <= '9') // skip spaces and nulls
		    {
			    returnValue = (returnValue * 10) + (value - (byte)'0');
		    }
	    }

	    return returnValue;
    }



    public static class UnitTest extends com.cboe.testFramework.UnitTestBaseImpl
    {
	public static void main(String [] args)
	{
	    com.cboe.testFramework.TestRunner.run(new UnitTest(), args);
	}
	public void testStringToDate() throws ParseException
	{
	    java.text.DateFormat formatter =  java.text.DateFormat.getInstance();
	    Calendar can = Calendar.getInstance(TimeZone.getTimeZone("CST"));
	    formatter.setCalendar(can);
	    Date date = formatter.parse("07/02/1930 4:43 PM");
	    Date result = FixUtilDateTimeFormatter.getLocalDate("19300702-21:43:00");
	    testContext.assertEquals("Date result is not correct.", date, result);
	    result = FixUtilDateTimeFormatter.getLocalDate("19300702");
	    can.setTime(result);
	    testContext.assertEquals( 6, can.get(Calendar.MONTH) );
	    testContext.assertEquals( 2, can.get(Calendar.DAY_OF_MONTH) );
	    testContext.assertEquals( 1930, can.get(Calendar.YEAR) );
	}
	public void testBackAndForthTranslation() throws ParseException
	{
	    java.text.DateFormat formatter =  java.text.DateFormat.getInstance();
	    Calendar can = Calendar.getInstance(TimeZone.getTimeZone("CST"));
	    formatter.setCalendar(can);
	    Date date = formatter.parse("07/02/1930 4:43 PM");
	    String result = FixUtilDateTimeFormatter.getFixTime(date);
	    Date date2 = FixUtilDateTimeFormatter.getLocalDate(result);
	    testContext.assertEquals(date, date2);
	}
	public void testCstTranslation() throws ParseException
	{
	    java.text.DateFormat formatter =  java.text.DateFormat.getInstance();
	    Calendar can = Calendar.getInstance(TimeZone.getTimeZone("CST"));
	    formatter.setCalendar(can);
	    Date date = formatter.parse("07/02/1930 4:43 PM");
	    String result = FixUtilDateTimeFormatter.getFixTime(date);
	    testContext.assertEquals("19300702-21:43:00", result);
	}
	public void testFormat() throws ParseException
	{
	    Date date = java.text.DateFormat.getInstance().parse("07/02/1930 4:43 PM");
	    Calendar can = Calendar.getInstance();
	    can.setTime(date);
	    String result = FixUtilDateTimeFormatter.time(can);
	    testContext.assertEquals("19300702-16:43:00", result);
	}
    }
}
