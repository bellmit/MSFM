package com.cboe.domain.util;

// import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.util.FormatNotFoundException;



/**
 * A wrapper to make it easier to use <code>Date</code>, <code>DateStruct</code>,
 * <code>TimeStruct</code> and <code>DateTimeStruct</code>.
 * <p>
 * The values returned by this class have not been cloned to make the class
 * immutable.  Changing returned values may cause strange results.
 * </p>
 *
 * @author John Wickberg
 */
public class DateWrapper
{
    /**
     * Name of the default date format
     */
    public static String DEFAULT_FORMAT_NAME = "Default";
    /**
     * Default date format
     */
    public static String DEFAULT_DATE_FORMAT = "yyyyMMdd HH:mm:ss";

    /**
     * Name and format used betwee CBOEdirect and LinkageServer for Linkage orders.
     */
    public static String FIX_LINKAGE_DATE_FORMAT_NAME = "FixLinkageDateFormat";
    public static String FIX_LINKAGE_DATE_FORMAT = "yyyyMMdd-HH:mm:ss.SSS";
    /**
     * Date/time value as <code>java.util.Date</code>.  May not be
     * valid.
     */
    private Date javaDate;
    /**
     * Indicates whether or not <code>javaDate</code> is valid.
     */
    private boolean validDate;
    /**
     * Date value as a struct.  May not be valid.
     */
    private DateStruct dateValues;
    /**
     * Indicates whether or not <code>dateValues</code> is valid.
     */
    private boolean validDateStruct;
    /**
     * Time value as a struct.  May not be valid.
     */
    private TimeStruct timeValues;
    /**
     * Indicates whether or not <code>javaDate</code> is valid.
     */
    private boolean validTimeStruct;
    /**
     * Table of <code>SimpleDateFormat</code>'s that are stored by
     * name.
     */
    private static Hashtable formatters;
    /**
     * Cache of <code>Calendar</code>'s that are stored by Thread.
     * A calendar for a wrapper can be obtained via <code>getCalendar</code>
     * and this calendar can be used without synchronization.  However, the
     * calendar should not be relied upon across calls to wrapper methods.
     */
    private static ThreadLocal calendars;
    /**
     * Cache of <code>DateWrapper</code>'s that are stored by Thread.  These
     * instances will be used int the static conversion methods.
     */
    private static ThreadLocal tempDateWrappers;

    /**
     * Millisecond clock as of current date this class loaded at midnight time. 
     */
    private static long         baselineMillis; 
    
    /**
     * the current date reloaded loaded at midnight time. 
     */
    private static DateStruct baselineDateStruct1;     
    
    private static DateStruct baselineDateStruct2;     
    
    
    private static boolean _testing=false;
    private static long _currentTimeMillis;
    private int hashcode;
    
    // initialize the hash tables and add the default format
    static
    {
        formatters = new Hashtable();
        calendars = new ThreadLocal();
        tempDateWrappers = new ThreadLocal();
        
        addDateFormatter(DEFAULT_FORMAT_NAME, DEFAULT_DATE_FORMAT);
        addDateFormatter(FIX_LINKAGE_DATE_FORMAT_NAME, FIX_LINKAGE_DATE_FORMAT);
        computeBaseline();
    }
    
    
    public static void  setCurrentTimeMillis(long currentTimeMillis){
        _currentTimeMillis=currentTimeMillis;
    }
    
    private static long currentTimeMillis(){
        if(_testing){
            return _currentTimeMillis;
        }
        return System.currentTimeMillis();
    }
    
    public static void setTesting(boolean tesing){
        _testing=tesing;
    }
/**
 * Creates a instance set to the current time.
 */
public DateWrapper()
{
    this(getCurrentDateTime());
    hashcode = Math.abs((int) getTimeInMillis());
}
/**
 * Creates an instance with the given time.
 *
 * @param timeInMillis time for instance
 */
public DateWrapper(long timeInMillis)
{
    super();
    hashcode = Math.abs((int) getTimeInMillis());
    if (timeInMillis != 0)
    {
        setDate(new java.util.Date(timeInMillis));
    }
    else
    {
        setDateTime(StructBuilder.buildDateTimeStruct());
    }
}
/**
 * Creates an instance using the date values.  The time will default to midnight.
 *
 * @param date DateStruct
 */
public DateWrapper(DateStruct date)
{
    super();
    hashcode = Math.abs((int) getTimeInMillis());
    setDate(date);
}
/**
 * Creates an instance using the date/time values.
 *
 * @param dateTime CORBA struct containing date and time
 */
public DateWrapper(DateTimeStruct dateTime)
{
    super();
    hashcode = Math.abs((int) getTimeInMillis());
    setDateTime(dateTime);
}
/**
 * Creates an instance using the time values.  The date will be set tp
 * the current day.
 *
 * @param time CORBA struct containing time values
 */
public DateWrapper(TimeStruct time)
{
    this();
    hashcode = Math.abs((int) getTimeInMillis());
    setTime(time);
}
/**
 * Creates a <code>DateWrapper</code> from a <code>Date</code>.
 *
 * @param aDate date to be wrapped
 */
public DateWrapper(Date aDate)
{
    super();
    hashcode = Math.abs((int) getTimeInMillis());
    setDate(aDate);
}
/**
 * Adds a <code>SimpleDateFormat</code> with the given name and pattern to the
 * cached formatters.
 *
 * @see #format()
 * @see #format(String)
 *
 * @param formatName name assigned for date format
 * @param formatPattern date formating pattern
 */
public static void addDateFormatter(String formatName, String formatPattern)
{
    SimpleDateFormat cachedFormatter=(SimpleDateFormat)formatters.get(formatName);
    
    if(cachedFormatter==null){
        SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);
        formatters.put(formatName, formatter);
    }
}
/**
 * Compares this DateWrapper to another DateWrapper
 *
 * @param anotherDateWrapper the DateWrapper to be compared
 * @return the value <code>0</code> if anotherDateWrapper
 *         is equal to this DateWrapper; a value less than
 *         <code>0</code> if this DateWrapper is less than the
 *         anotherDateWrapper; and a value greater than
 *         <code>0</code> if this DateWrapper is greater than the
 *         anotherDateWrapper
 */
public int compareTo( DateWrapper anotherDateWrapper )
{
   // Convert this DateWrapper to millis
   long thisVal = getTimeInMillis( );

   // Convert the argument DateWrapper to millis
   long anotherVal = anotherDateWrapper.getTimeInMillis( );

   // Return the result
   return ( thisVal < anotherVal ? -1 : ( thisVal == anotherVal ? 0 : 1 ));
}
/**
 * Converts the <code>Date</code> of this wrapper to the structs that aren't
 * already set.
 */
private void convertDateToStruct()
{
    Calendar cal = getCalendar();
    if (!validDateStruct)
    {
        validDateStruct = true;
        if (javaDate.getTime() != 0)
        {
            dateValues = new DateStruct();
            dateValues.year = (short) cal.get(Calendar.YEAR);
            // use 1 based month rather than 0 based month
            dateValues.month = (byte) (cal.get(Calendar.MONTH) + 1);
            dateValues.day = (byte) cal.get(Calendar.DATE);
        }
        else
        {
            dateValues = StructBuilder.buildDateStruct();
        }
    }
    if (!validTimeStruct)
    {
        validTimeStruct = true;
        if (javaDate.getTime() != 0)
        {
            timeValues = new TimeStruct();
            timeValues.hour = (byte) cal.get(Calendar.HOUR_OF_DAY);
            timeValues.minute = (byte) cal.get(Calendar.MINUTE);
            timeValues.second = (byte) cal.get(Calendar.SECOND);
            // truncate milliseconds to fit in hundredths. This is required since TimeStruct.fraction is byte.              
            //This rounding to be removed, TimeStruct.fraction needs to be changed short, which will involve cmi idl changes. 
            timeValues.fraction = (byte) (cal.get(Calendar.MILLISECOND) / 10);
        }
        else
        {
            timeValues = StructBuilder.buildTimeStruct();
        }
    }
}
/**
 * Converts the valid <code>DateStruct</code> and/or <code>TimeStruct</code>
 * to a <code>Date</code>.
 */
private void convertStructToDate()
{
    Calendar cal = getCalendar(false);
    boolean defaultDate = false;
    cal.clear();
    if (validDateStruct)
    {
        if (!StructBuilder.isDefault(dateValues))
        {
            // struct contains 1 based months
            cal.set(dateValues.year, dateValues.month - 1, dateValues.day);
        }
        else
        {
            defaultDate = true;
        }
    }
    else
    {
        // set to first day of java calendar
        cal.set(1970, 1, 1);
    }
    if (validTimeStruct && !defaultDate)
    {
        if (!StructBuilder.isDefault(timeValues))
        {
            cal.set(Calendar.HOUR_OF_DAY, timeValues.hour);
            cal.set(Calendar.MINUTE, timeValues.minute);
            cal.set(Calendar.SECOND, timeValues.second);
            // fraction is in hundredths
            //This is required since TimeStruct.fraction is byte.              
            //This rounding to be removed, TimeStruct.fraction needs to be changed short, which will involve cmi idl changes.            
            cal.set(Calendar.MILLISECOND, timeValues.fraction * 10);
        }
        else
        {
            defaultDate = true;
        }
    }
    // don't want to call setDate method since it resets flags
    if (defaultDate)
    {
        javaDate = new Date(0);
    }
    else
    {
        javaDate = cal.getTime();
    }
    validDate = true;
}
/**
 * Convenience method for converting a time in millis to a DateStruct.
 *
 * @param aTime a time in millis to be converted to a struct
 * @return struct corresponding to time
 */
public static DateStruct convertToDate(long aTime)
{
    
    boolean secured=ensureBaseline();
    
    if(secured){
        long diff=aTime-baselineMillis;
        if(diff>0 && diff<MS_PER_DAY){
            return baselineDateStruct2;
        }
    }
    
    DateStruct result;
    if (aTime != 0)
    {
        DateWrapper wrap = getTempDateWrapper();
        wrap.setTimeInMillis(aTime);
        result = wrap.toDateStruct();
    }
    else
    {
        result = StructBuilder.buildDateStruct();
    }
    return result;
}
/**
 * Convenience method for converting a time in millis to a DateTimeStruct.
 *
 * @param aTime a time in millis to be converted to a struct
 * @return struct corresponding to time
 */
public static DateTimeStruct convertToDateTime(long aTime)
{
    DateTimeStruct result;
    boolean secured=ensureBaseline();
    
    if(secured){
        long diff=aTime-baselineMillis;
        if( diff>0 && diff<MS_PER_DAY){
            result = new DateTimeStruct();
            result.date=baselineDateStruct2;
            result.time=getTimeStruct(aTime);
            return result;
        }
    }
    
    if (aTime != 0)
    {
        DateWrapper wrap = getTempDateWrapper();
        wrap.setTimeInMillis(aTime);
        result = wrap.toDateTimeStruct();
    }
    else
    {
        result = StructBuilder.buildDateTimeStruct();
    }
    return result;
}
/**
 * Convenience method for converting a DateStruct to a time in millis.
 *
 * @param aDate struct to be converted
 * @return time in millis
 */
public static long convertToMillis(DateStruct aDate)
{
    boolean secured=ensureBaseline();
    if(secured && aDate.year==baselineDateStruct1.year 
            && aDate.month==baselineDateStruct1.month 
            && aDate.day==baselineDateStruct1.day)
    {
            return baselineMillis;
    }
    
    long result = 0;
    if (aDate != null && !StructBuilder.isDefault(aDate))
    {
        DateWrapper wrap = getTempDateWrapper();
        wrap.setDate(aDate);
        result = wrap.getTimeInMillis();
    }
    return result;
}
/**
 * Convenience method for converting a DateTimeStruct to a time in millis.
 *
 * @param aDateTime struct to be converted
 * @return time in millis
 */
public static long convertToMillis(DateTimeStruct aDateTime)
{
    // stop using cached millis 3 secs before midnight
    boolean secured=ensureBaseline();
    if( secured && aDateTime.date.year==baselineDateStruct1.year 
            && aDateTime.date.month==baselineDateStruct1.month 
            && aDateTime.date.day==baselineDateStruct1.day)
    {
        return  baselineMillis+aDateTime.time.hour*MS_PER_HOUR+aDateTime.time.minute*MS_PER_MINUTE
        +aDateTime.time.second*MS_PER_SECOND+aDateTime.time.fraction*MS_PER_100TH_SEC;    
    }
    
    long result = 0;
    if (aDateTime != null && !StructBuilder.isDefault(aDateTime))
    {
        DateWrapper wrap = getTempDateWrapper();
        wrap.setDateTime(aDateTime);
        result = wrap.getTimeInMillis();
    }
    return result;
    

    
}
/**
 * Convenience method for converting a TimeStruct to a time in millis.
 *
 * @param aTime struct to be converted
 * @return time in millis
 */
public static long convertToMillis(TimeStruct aTime)
{
//    long result = 0;
//    if (aTime != null && !StructBuilder.isDefault(aTime))
//    {
//        DateWrapper wrap = getTempDateWrapper();
//        // get current time so result will be based on current date.
//        wrap.setTimeInMillis(getCurrentDateTime());
//        wrap.setTime(aTime);
//        result = wrap.getTimeInMillis();
//    }
//    return result;
    
    // use baselineMillis+offset instead of calendar operation
    // if day change occurs right after ensureBaselineMillis() then 
    // the result will be off by a day, but the previous implementation
    // has the same issue
    ensureBaseline();
    //System.out.println("baselineMillis="+baselineMillis);
    
    return baselineMillis+aTime.hour*MS_PER_HOUR+aTime.minute*MS_PER_MINUTE
    +aTime.second*MS_PER_SECOND+aTime.fraction*MS_PER_100TH_SEC;
    
}
/**
 * Convenience method for converting a time in millis to a DateStruct.
 *
 * @param aTime a time in millis to be converted to a struct
 * @return struct corresponding to time
 */
public static TimeStruct convertToTime(long aTime)
{
    return getTimeStruct(aTime);

    //  FORMER Old code, now copied into else case of getTimeStruct().
    //  I did this delegation here to avoid some more extensive refactoring.
    //    TimeStruct result;
    //  if (aTime != 0)
    //  {
    //      DateWrapper wrap = new DateWrapper(aTime);
    //      result = wrap.toTimeStruct();
    //  }
    //  else
    //  {
    //      result = StructBuilder.buildTimeStruct();
    //  }
    //  return result;
}
/**
 * Performs equality check.
 */
public boolean equals(Object otherDate) {
    if (otherDate instanceof DateWrapper) {
        return getTimeInMillis() == ((DateWrapper) otherDate).getTimeInMillis();
    }
    return false;
}
/**
 * Formats the date using the default date format.
 *
 * @return formatted date
 */
public String format()
{
    try
    {
        return format(DEFAULT_FORMAT_NAME);
    }
    catch (FormatNotFoundException e)
    {
        // should not happen, but...
        throw new NullPointerException("Could not find default date formatter.");
    }
}
/**
 * Formats the date using the named formatter.
 *
 * @see #addDateFormatter
 *
 * @param formatName name of date formatter
 * @return formatted date
 */
public String format(String formatName) throws FormatNotFoundException
{
    SimpleDateFormat formatter = getFormatter(formatName);
    String result;
    synchronized (formatter)
    {
        result = formatter.format(getDate());
    }
    return result;
}
/**
 * Returns a <code>Calendar</code> that can be used to manipulate this date.
 *
 * @return Calendar dedicated to current thread
 */
public Calendar getCalendar()
{
    return getCalendar(true);
}
/**
 * Returns a <code>Calendar</code> that can be used to manipulate this date.  The
 * returned Calendar will be dedicated to the current thread, so it can be used
 * without synchronization.
 *
 * @param setWithDate if <code>true</code>, the value of the calendar will be
 *                    set with the date of this wrapper.  Flag was needed to
 *                    break a recursion loop.
 * @return Calendar dedicated to current thread
 */
private Calendar getCalendar(boolean setWithDate)
{
    Calendar result = (Calendar) calendars.get();
    if (result == null)
    {
        result = Calendar.getInstance();
        calendars.set(result);
    }
    if (setWithDate)
    {
        result.setTime(getDate());
    }
    return result;
}
/**
 * Gets current date/time from time service.
 */
private static long getCurrentDateTime() {
    // Can't currently use TimeService, this class is used in GUI and FF is
    // not part of code shipped with the GUI.
    //
    // return FoundationFramework.getInstance().getTimeService().getCurrentDateTime();
    return currentTimeMillis();
}
/**
 * Returns the <code>Date</code> of this wrapper.
 *
 * @return date of this wrapper
 */
public Date getDate()
{
    if (!validDate)
    {
        convertStructToDate();
    }
    return javaDate;
}
/**
 * Returns the <code>SimpleDateFormat</code> having the given name.
 *
 * @param formatName name of date format
 * @return date formatter with given name
 * @exception FormatNotFoundException if requested format doesn't exist
 */
private static SimpleDateFormat getFormatter(String formatName) throws FormatNotFoundException
{
    SimpleDateFormat formatter = (SimpleDateFormat) formatters.get(formatName);
    if (formatter == null)
    {
        throw new FormatNotFoundException("No formatter defined with name = " + formatName);
    }
    return formatter;
}
/**
 * Returns a <code>Calendar</code> that can be used to manipulate this date.  The
 * returned Calendar will <code>Not</code> be dedicated to the current thread,
 * and the value of the calendar will be set with the date of this wrapper.
 *
 * @return Calendar
 */
public Calendar getNewCalendar()
{
    Calendar result = Calendar.getInstance();
    result.setTime(getDate());
    return result;
}
/**
 * Gets temp date wrapper.
 */
private static DateWrapper getTempDateWrapper() {
    DateWrapper result = (DateWrapper) tempDateWrappers.get();
    if (result == null) {
        result = new DateWrapper();
        tempDateWrappers.set(result);
    }
    return result;
}
/**
 * Returns the time of this wrapper as time in milliseconds.
 *
 * @return long time in milliseconds
 */
public long getTimeInMillis()
{
    return getDate().getTime();
}
/**
 * Calculates hash code from time.
 */
public int hashCode() {
    return hashcode;
}
/**
 * Parses formatted date value using the default format.
 *
 * @param formattedValue date formatted according to default format
 * @return parsed date as a <code>DateWrapper</code>
 * @exception ParseException if value being parsed doesn't match format
 */
public static DateWrapper parse(String formattedValue) throws ParseException
{
    try
    {
        return parse(DEFAULT_FORMAT_NAME, formattedValue);
    }
    catch (FormatNotFoundException e)
    {
        // should not happen, but..
        throw new NullPointerException("Could not find default date format");
    }
}
/**
 * Parses a formatted string using the named date format.
 *
 * @param formatName name of format to use for parsing
 * @param formattedValue value formatted according to named format
 * @return parsed date as a <code>DateWrapper</code>
 * @exception FormatNotFoundException if date format doesn't exist
 * @exception ParseException if value being parsed doesn't match format
 */
public static DateWrapper parse(String formatName, String formattedValue) throws FormatNotFoundException, ParseException
{
    SimpleDateFormat formatter = getFormatter(formatName);
    Date newDate;
    synchronized (formatter)
    {
        newDate = formatter.parse(formattedValue);
    }
    return new DateWrapper(newDate);
}
/**
 * Resets all valid flags for parts of this date wrapper.
 */
private void resetFlags()
{
    validDate = false;
    validDateStruct = false;
    validTimeStruct = false;
}
/**
 * Sets the value of this wrapper from a <code>DateStruct</code>.  The time portion
 * of this wrapper will be set to midnight.
 *
 * @param newDate new date value
 */
public void setDate(DateStruct newDate)
{
    if (newDate == null)
    {
        throw new IllegalArgumentException("Date struct cannot be null");
    }
    resetFlags();
    dateValues = newDate;
    validDateStruct = true;
}
/**
 * Sets the value of this wrapper from a <code>Calendar</code>.
 *
 * @param dateCalendar new date value
 */
public void setDate(Calendar dateCalendar)
{
    setDate(dateCalendar.getTime());
}
/**
 * Sets the value of this wrapper from a <code>Date</code>.
 *
 * @param newDate new date value
 */
public void setDate(java.util.Date newDate)
{
    resetFlags();
    javaDate = newDate;
    validDate = true;
}
/**
 * Sets the value of this wrapper from a <code>DateTimeStruct</code>.
 *
 * @param dateTime new date/time value
 */
public void setDateTime(DateTimeStruct dateTime)
{
    if (dateTime == null)
    {
        throw new IllegalArgumentException("DateTime struct cannot be null");
    }
    resetFlags();
    validDateStruct = true;
    dateValues = dateTime.date;
    validTimeStruct = true;
    timeValues = dateTime.time;
}
/**
 * Sets the time value of this wrapper from a <code>TimeStruct</code>.  The date portion
 * of this wrapper will be unchanged.
 *
 * @param time new time value
 */
public void setTime(TimeStruct time)
{
    if (time == null)
    {
        throw new IllegalArgumentException("Time struct cannot be null");
    }
    if (!validDateStruct && validDate)
    {
        convertDateToStruct();
    }
    validTimeStruct = true;
    validDate = false;
    timeValues = time;
}
/**
 * Sets the value of this wrapper from a time in milliseconds.
 *
 * @param time time in milliseconds
 */
public void setTimeInMillis(long time)
{
    setDate(new Date(time));
}
/**
 * Returns a <code>DateStruct</code> that represents the date portion of this
 * wrapper.
 *
 * @return struct containing date values
 */
public DateStruct toDateStruct()
{
    if (!validDateStruct)
    {
        convertDateToStruct();
    }
    return dateValues;
}
/**
 * Returns a <code>DateTimeStruct</code> that represents this wrapper.
 *
 * @return struct containing wrapper values
 */
public DateTimeStruct toDateTimeStruct()
{
    if (!validDateStruct || !validTimeStruct)
    {
        convertDateToStruct();
    }
    return new DateTimeStruct(dateValues, timeValues);
}
/**
 * Returns a <code>TimeStruct</code> that represents the time portion of this
 * wrapper.
 *
 * @return struct containing time values
 */
public TimeStruct toTimeStruct()
{
    if (!validTimeStruct)
    {
        convertDateToStruct();
    }
    return timeValues;
}

    private synchronized static final void computeBaseline() {
        long                midnightTime    = 0;
        if (baselineDateStruct1==null)
            baselineDateStruct1=StructBuilder.buildDateStruct();
        if (baselineDateStruct2==null)
            baselineDateStruct2=StructBuilder.buildDateStruct();  
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        baselineDateStruct2.year=baselineDateStruct1.year = (short) year;
        baselineDateStruct2.month=baselineDateStruct1.month = (byte) (cal.get(Calendar.MONTH) + 1);
        baselineDateStruct2.day=baselineDateStruct1.day = (byte) cal.get(Calendar.DATE);
        
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear); // (cal is now midnight of current day)
        baselineMillis      = cal.getTimeInMillis();
        
        
        
    }
    
    private static final long    MS_PER_DAY          = 24 * 60 * 60 * 1000;
    private static final int     MS_PER_HOUR         = 60 * 60 * 1000;
    private static final int     MS_PER_MINUTE       = 60 * 1000;
    private static final int     MS_PER_SECOND       = 1000;
    private static final int     MS_PER_100TH_SEC    = 10;     
    

    private static final TimeStruct getTimeStruct(long p_timeInMillis) {

        /*
         * The OLD CBOE Direct DateWrapper.convertToTime(long aTime) code was 
         * pretty spendy. It created 3 extra objects to get a TimeStruct, and 
         * uses the Java Calendar to figure out the hour, minute and second 
         * from the timeInMillis. The Calendar is VERY expensive since it does 
         * lots of modulus and division to figure out leap years from 1970, and 
         * to figure out year, month, day, day of week, hour, minute, second 
         * and lots of other stuff we don't need.
         * 
         * We will do an optimization which exploits how this method is most used.
         * 99%+ of all calls to this method will be with timestamps from TODAY.
         * This means we can do a "clever cheat" which avoids the Calendar.
         * 
         * We'll first compute millisSinceMidnight = timeInMillis - todaysMidnightMillis.
         * If this is >= 0 and < MILLIS_IN_A_DAY, this means millisSinceMidnight
         * is a relative time since midnight TODAY, and therefore timeInMillis is
         * a timestamp from TODAY. To compute hr, min, sec and fraction is then
         * simple; we use simple division and modulus to obtain the values.
         * If the millisSinceMidnight is < 0 or > MILLIS_IN_A_DAY, this means
         * it is not from today, so we'll use the old code that uses the Calendar
         * to get the TimeStruct instead. 
         * 
         * It is expected this method won't be called much with times outside 
         * of today, however, so we'll do a MUCH faster code path for the most likely
         * case. 
         */
        
        TimeStruct   ts;
        long         millisSinceMidnight;
        int          millis, hr, min, sec, frac;
        
        millisSinceMidnight = p_timeInMillis - baselineMillis;
        
        if (millisSinceMidnight >= 0 && millisSinceMidnight < MS_PER_DAY) {
            
            // Use 4 byte integer math; should run faster on 32 bit CPUs
            millis   = (int)millisSinceMidnight;   

            // Divide millis by the appropriate divisor to get each value, 
            // then modulus to remove that value from millis to get the next 
            // value, etc.  Use subtract and multiply to do modulus; this should 
            // run faster than "%" modulus which is a form of division.
            hr       = millis / MS_PER_HOUR;
            millis   = millis - (hr * MS_PER_HOUR);

            min      = millis / MS_PER_MINUTE;
            millis   = millis - (min * MS_PER_MINUTE);
            
            sec      = millis / MS_PER_SECOND;
            millis   = millis - (sec * MS_PER_SECOND);
            
            // NOTE: CboeDir DateWrapper TRUNCATES the millis to 100ths by using division!!!
            //       We do the same so we have identical behavior.
            frac     = millis / MS_PER_100TH_SEC;    
            
            ts = new TimeStruct((byte)hr,(byte)min,(byte)sec,(byte)frac);
        }
        else { 
            // Revert to old way of doing things.
            if (p_timeInMillis != 0) {
                DateWrapper wrap = new DateWrapper(p_timeInMillis);
                ts = wrap.toTimeStruct();
            }
            else {
                ts = StructBuilder.buildTimeStruct();
            }
        }
        return ts;
    }
    
    
    // worst case is computeBaselineMillis being called more than once right after midnight
    private final static boolean ensureBaseline(){
        long cutover=baselineMillis+MS_PER_DAY;
        long now=currentTimeMillis();
       
        if(now>=cutover){
            if(!_testing)
                computeBaseline();
            else{// purely for testing purpose
                baselineMillis=cutover;
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                int year = cal.get(Calendar.YEAR);
                baselineDateStruct2.year=baselineDateStruct1.year = (short) year;
                baselineDateStruct2.month=baselineDateStruct1.month = (byte) (cal.get(Calendar.MONTH) + 1);
                baselineDateStruct2.day=baselineDateStruct1.day = (byte) cal.get(Calendar.DATE);
            }
                
                
        }
        //System.out.println("ensureBaseline baselineMillis="+baselineMillis+" now="+now+" cutover="+cutover+" baselineDate="+(new Date(baselineMillis)));
        baselineDateStruct2.year=baselineDateStruct1.year;
        baselineDateStruct2.month=baselineDateStruct1.month;
        baselineDateStruct2.day=baselineDateStruct1.day;        
        // stop using cached time 3 secs before midnight, 
        return now+3000<cutover;
    }
 
 
    /**
     * Helper method to compute the number of Milliseconds after midnight.
     * @return
     */
    public static long getMillisAfterMidnight()
    {
        long now = System.currentTimeMillis();
        ensureBaseline();
        return now - baselineMillis;
    }
    
}