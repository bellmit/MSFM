package com.cboe.domain.util;

import com.cboe.idl.cmiUtil.*;
import com.cboe.util.FormatNotFoundException;
import java.util.*;
import java.text.*;
import com.cboe.infrastructureServices.foundationFramework.*;
/**
 * A wrapper to make it easier to use the Foundation FrameWork TimerService to convert
 * to <code>Date</code>, <code>DateStruct</code>,
 * <code>TimeStruct</code> and <code>DateTimeStruct</code>.
 *
 * @author Connie Feng
 */
public class TimeServiceWrapper
{
    private static String TIMER_DATE_FORMAT = "TimerServiceDateFormat";
    private static String TIMER_DATE_FORMAT_PATTERN = "yyyyMMdd";

    /**
     * private constructor
     */
    private TimeServiceWrapper()
    {
    }

    public static com.cboe.infrastructureServices.timeService.TimeService getService()
    {
     	return FoundationFramework.getInstance().getTimeService();
    }

    public static DateStruct toDateStruct()
    {
    	return DateWrapper.convertToDate(getService().getCurrentDateTime());
    }
    /**
     * Convenience method for converting a time in millis to a DateTimeStruct.
     *
     * @param aTime a time in millis to be converted to a struct
     * @return struct corresponding to time
     */
    public static DateTimeStruct toDateTimeStruct()
    {
    	return DateWrapper.convertToDateTime(getService().getCurrentDateTime());
    }

    /**
     * Convenience method for converting current time in millis to a TimeStruct.
     *
     * @param aTime a time in millis to be converted to a struct
     * @return struct corresponding to time
     */
    public static TimeStruct toTimeStruct()
    {
        return DateWrapper.convertToTime(getService().getCurrentDateTime());
    }

    /**
     * Convenience method for converting get current system time in millis.
     *
     * @return time in millis
     */
    public static long getCurrentDateTimeInMillis()
    {
    	return getService().getCurrentDateTime();
    }

    /**
     * Returns a <code>Calendar</code> that can be used to manipulate this date.
     *
     * @return Calendar dedicated to current thread
     */
    public static Calendar getCalendar()
    {
    	DateWrapper dateWrapper = new DateWrapper(getService().getCurrentDateTime());
        return dateWrapper.getCalendar();
    }

    /**
     * Convenience method for converting current time into "YYYYMMDD" format.
     *
     * @return String
     */
    public static String formatToDate()
    {
        try
        {
            DateWrapper dateWrapper = new DateWrapper(getService().getCurrentDateTime());

            dateWrapper.addDateFormatter(TIMER_DATE_FORMAT, TIMER_DATE_FORMAT_PATTERN);

            return dateWrapper.format(TIMER_DATE_FORMAT);
        }
        catch (FormatNotFoundException e)
	    {
		    // should not happen, but...
		    throw new NullPointerException("Could not find date formatter: " + TIMER_DATE_FORMAT);
	    }
    }

    /**
     * Convenience method for converting current system time in millis to date time format.
     * "yyyyMMdd hh:mm:ss"
     * @return String
     */
    public static String formatToDateTime()
    {
        return new DateWrapper(getService().getCurrentDateTime()).format();
    }
}
