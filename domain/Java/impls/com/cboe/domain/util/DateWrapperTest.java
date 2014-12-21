package com.cboe.domain.util;

import junit.framework.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.util.FormatNotFoundException;
import java.util.*;

/**
 * A unit tester for the <code>DateWrapper</code> class.
 * 
 * @author John Wickberg
 */
public class DateWrapperTest extends TestCase
{
/**
 * Creates a test.
 *
 * @param name test name
 *
 * @author John Wickberg
 */
    
    
private static long pointInTime=System.currentTimeMillis();
private static final long bodToday;
private static final long eodToday;
private static final long    MS_PER_DAY          = 24 * 60 * 60 * 1000;

static {
    Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);
    int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
    cal.clear();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.DAY_OF_YEAR, dayOfYear); // (cal is now midnight of current day)
    bodToday      = cal.getTimeInMillis();
    eodToday      = bodToday+MS_PER_DAY;
    DateWrapper.setTesting(true);
    DateWrapper.setCurrentTimeMillis(pointInTime);
}

public DateWrapperTest(String name)
{
	super(name);
}



public void setTargetTimeToBOD(){
    pointInTime=bodToday;
    DateWrapper.setCurrentTimeMillis(pointInTime);
    //System.out.println("setCurrTimeToBOD called");
}

public void setTargetTimeToEOD(){
    pointInTime=eodToday;
    DateWrapper.setCurrentTimeMillis(pointInTime);
    //System.out.println("setCurrTimeToEOD called pointInTime="+pointInTime);
}

public void setTargetTimeToRightAfterEOD(){
    pointInTime=eodToday+10;
    DateWrapper.setCurrentTimeMillis(pointInTime);
    //System.out.println("setCurrTimeToRightAfterEOD called");
}

public void setTargetTimeToRightBeforeEOD(){
    pointInTime=eodToday-1000;
    DateWrapper.setCurrentTimeMillis(pointInTime);
    //System.out.println("setCurrTimeToRightBeforeEOD called");
}

public void setCurrTime(){
    pointInTime=System.currentTimeMillis();
    DateWrapper.setCurrentTimeMillis(pointInTime);
    //System.out.println("setCurrTime called");
}



/**
 * Tests creating a DateWrapper from a DateStruct.
 * 
 * @author John Wickberg
 */
public void testCreateFromDateStruct()
{
//	DateStruct date = new DateStruct();
//	date.year = 1998;
//	date.month = 12;
//	date.day = 14;
	
	DateStruct date = this.millis2DateTimeStruct(pointInTime).date;
	DateWrapper wrap = new DateWrapper(date);
	// force conversion to a Date
	Date result = wrap.getDate();
	// set value of wrapper from Date
	wrap.setDate(result);
	// force conversion of Date to DateStruct
	DateStruct resultStruct = wrap.toDateStruct();
	// verify round trip conversions
	assertTrue("Verify Year", date.year == resultStruct.year);
	assertTrue("Verify Month", date.month == resultStruct.month);
	assertTrue("Verify Day", date.day == resultStruct.day);
}
/**
 * Tests creation of date from time in millis.
 * 
 * @author John Wickberg
 */
public void testCreateFromMillis()
{
	long time = pointInTime;
	DateWrapper wrap = new DateWrapper(time);
	// force conversion to DateTimeStruct
	DateTimeStruct result = wrap.toDateTimeStruct();
	// reset from struct to force round trip conversion
	wrap.setDateTime(result);
	// need to ignore last digit since it was lost in round trip conversions
	assertTrue("Time is equal", time / 10 == wrap.getTimeInMillis() / 10);
}
/**
 * Tests creating a DateWrapper from a TimeStruct.
 * 
 * @author John Wickberg
 */
public void testCreateFromTimeStruct()
{
//	TimeStruct time = new TimeStruct();
//	time.hour = 14;
//	time.minute = 15;
//	time.second = 16;
//	time.fraction = 17;
	TimeStruct time = millis2DateTimeStruct(pointInTime).time;
	DateWrapper wrap = new DateWrapper(time);
	// force conversion of TimeStruct to Date
	Date result = wrap.getDate();
	wrap.setDate(result);
	// force conversion back to TimeStruct
	TimeStruct resultStruct = wrap.toTimeStruct();
	// version round trip conversions
	assertTrue("Verify hour", time.hour == resultStruct.hour);
	assertTrue("Verify minute", time.minute == resultStruct.minute);
	assertTrue("Verify second", time.second == resultStruct.second);
	assertTrue("Verify fraction", time.fraction == resultStruct.fraction);
}
/**
 * Tests setting the wrapper with a default date time.
 * 
 * @author John Wickberg
 */
public void defaultDateTime()
{
	DateTimeStruct dateTime = StructBuilder.buildDateTimeStruct();
	DateWrapper wrap = new DateWrapper(dateTime);
	wrap.setDate(wrap.getDate());
	DateTimeStruct result = wrap.toDateTimeStruct();
	assertTrue("Default date time", StructBuilder.isDefault(result));
}
/**
 * Tests setting the wrapper with a default time.
 * 
 * @author John Wickberg
 */
public void defaultTime()
{
	TimeStruct time = StructBuilder.buildTimeStruct();
	DateWrapper wrap = new DateWrapper(time);
	wrap.setDate(wrap.getDate());
	TimeStruct result = wrap.toTimeStruct();
	assertTrue("Default time", StructBuilder.isDefault(result));
}
/**
 * Tests adding and using a date format.
 * 
 * @author John Wickberg
 */
public void formatDate() throws FormatNotFoundException, java.text.ParseException
{
	DateWrapper.addDateFormatter("TestFormat", "MMMM dd, yyyy");
	DateStruct date = new DateStruct();
	date.year = 1998;
	date.month = 12;
	date.day = 14;
	DateWrapper wrap = new DateWrapper(date);
	// Convert to string using format
	String result = wrap.format("TestFormat");
	// convert formatted string back to wrapper
	wrap = DateWrapper.parse("TestFormat", result);
	// convert back to a struct
	DateStruct resultStruct = wrap.toDateStruct();
	// Verify round trip conversions
	assertTrue("Same Year", date.year == resultStruct.year);
	assertTrue("Same Month", date.month == resultStruct.month);
	assertTrue("Same Day", date.day == resultStruct.day);
}
/**
 * Returns a suite of tests for unit testing DateWrapper.
 * 
 * @return suite of tests
 *
 * @author John Wickberg
 */
public static TestSuite suite()
{  
	TestSuite suite = new TestSuite();
	/*
	suite.addTest(new DateWrapperTest("setTargetTimeToBOD"));
	suite.addTest(new DateWrapperTest("testConvertToMillis"));
	suite.addTest(new DateWrapperTest("testConvertToStructs"));
	suite.addTest(new DateWrapperTest("testCreateFromMillis"));
	suite.addTest(new DateWrapperTest("testCreateFromDateStruct"));
	suite.addTest(new DateWrapperTest("testCreateFromTimeStruct"));
	suite.addTest(new DateWrapperTest("testUpdateTime"));
	suite.addTest(new DateWrapperTest("testSetDate"));
	suite.addTest(new DateWrapperTest("testSetDate"));
	suite.addTest(new DateWrapperTest("formatDate"));
	suite.addTest(new DateWrapperTest("defaultDateTime"));
	suite.addTest(new DateWrapperTest("defaultTime"));
 
    suite.addTest(new DateWrapperTest("setTargetTimeToEOD"));
    suite.addTest(new DateWrapperTest("testConvertToMillis"));
    suite.addTest(new DateWrapperTest("testConvertToStructs"));
    suite.addTest(new DateWrapperTest("testCreateFromMillis"));
    suite.addTest(new DateWrapperTest("testCreateFromDateStruct"));
    suite.addTest(new DateWrapperTest("testCreateFromTimeStruct"));
    suite.addTest(new DateWrapperTest("testUpdateTime"));
    suite.addTest(new DateWrapperTest("testSetDate"));
    suite.addTest(new DateWrapperTest("testSetDate"));
    suite.addTest(new DateWrapperTest("formatDate"));
    suite.addTest(new DateWrapperTest("defaultDateTime"));
    suite.addTest(new DateWrapperTest("defaultTime"));
*/
    suite.addTest(new DateWrapperTest("setTargetTimeToRightAfterEOD"));
    suite.addTest(new DateWrapperTest("testConvertToMillis"));
    suite.addTest(new DateWrapperTest("testConvertToStructs"));
    suite.addTest(new DateWrapperTest("testCreateFromMillis"));
    suite.addTest(new DateWrapperTest("testCreateFromDateStruct"));
    suite.addTest(new DateWrapperTest("testCreateFromTimeStruct"));
    suite.addTest(new DateWrapperTest("testUpdateTime"));
    suite.addTest(new DateWrapperTest("testSetDate"));
    suite.addTest(new DateWrapperTest("testSetDate"));
    suite.addTest(new DateWrapperTest("formatDate"));
    suite.addTest(new DateWrapperTest("defaultDateTime"));
    suite.addTest(new DateWrapperTest("defaultTime"));
    /*
    suite.addTest(new DateWrapperTest("setTargetTimeToRightBeforeEOD"));
    suite.addTest(new DateWrapperTest("testConvertToMillis"));
    suite.addTest(new DateWrapperTest("testConvertToStructs"));
    suite.addTest(new DateWrapperTest("testCreateFromMillis"));
    suite.addTest(new DateWrapperTest("testCreateFromDateStruct"));
    suite.addTest(new DateWrapperTest("testCreateFromTimeStruct"));
    suite.addTest(new DateWrapperTest("testUpdateTime"));
    suite.addTest(new DateWrapperTest("testSetDate"));
    suite.addTest(new DateWrapperTest("testSetDate"));
    suite.addTest(new DateWrapperTest("formatDate"));
    suite.addTest(new DateWrapperTest("defaultDateTime"));
    suite.addTest(new DateWrapperTest("defaultTime"));
    
    suite.addTest(new DateWrapperTest("setCurrTime"));
    suite.addTest(new DateWrapperTest("testConvertToMillis"));
    suite.addTest(new DateWrapperTest("testConvertToStructs"));
    suite.addTest(new DateWrapperTest("testCreateFromMillis"));
    suite.addTest(new DateWrapperTest("testCreateFromDateStruct"));
    suite.addTest(new DateWrapperTest("testCreateFromTimeStruct"));
    suite.addTest(new DateWrapperTest("testUpdateTime"));
    suite.addTest(new DateWrapperTest("testSetDate"));
    suite.addTest(new DateWrapperTest("testSetDate"));
    suite.addTest(new DateWrapperTest("formatDate"));
    suite.addTest(new DateWrapperTest("defaultDateTime"));
    suite.addTest(new DateWrapperTest("defaultTime"));
	*/
	return suite;
}
/**
 * Tests changing the time value of a DateWrapper.
 * 
 * @author John Wickberg
 */
public void testUpdateTime()
{
    //DateWrapper test = new DateWrapper();
    DateWrapper test = new DateWrapper(pointInTime);
	DateStruct date = test.toDateStruct();
	TimeStruct time = new TimeStruct((byte) 9, (byte) 0, (byte) 0, (byte) 0);
	test.setTime(time);
	// force conversions
	test.setDate(test.getDate());
	DateTimeStruct result = test.toDateTimeStruct();
	assertTrue("Date unchanged", result.date.day == date.day && result.date.month == date.month && result.date.year == date.year);
	assertTrue("Time matches", result.time.hour == time.hour && result.time.minute == time.minute && result.time.second == time.second && result.time.fraction == time.fraction);
}


/**
 * Test the following:
 * public DateWrapper(Date aDate)
 * public void setDate(Calendar dateCalendar)
 * public void setDate(DateStruct newDate)
 * public void setTimeInMillis(long time)
 * public void setTime(TimeStruct time)
 * public int compareTo( DateWrapper anotherDateWrapper )
 */
public void testSetDate(){
    DateWrapper d1= new DateWrapper(pointInTime);
    DateWrapper d2= new DateWrapper();
    d2.setDate(millis2Calendar(pointInTime));
    assertTrue("setDate(Calendar)",d1.compareTo(d2)==0);
    
    /* <aaa>-the following test would fail:
     *  -new DateWrapper() set a Date member to current time
     *  -setDate(DateStruct) set a DateStruct member, 
     *   set the valid flag for Date to false but 
     *   does NOT update it. 
     *  -compareTo compares Date.getTime() 
     *  suggested change to compareTo:
     *  if date is valid then use it to compare
     *  else if DateStruct and/or TimeStruct are valid 
     *  use them to compare. this can be costly because of
     *  Struct to millisec conversion. not sure if it worth it.
     *  moreover, a DateTimeStruct is only good up to 100 millis, 
     *  what's the rule for comparison btw DateTimeStruct and Date
     *  
     */  
    DateTimeStruct dt=millis2DateTimeStruct(pointInTime);
    DateWrapper d3= new DateWrapper();
    d3.setDate(dt.date);
    assertTrue("setDate(DateStruct)",d3.getTimeInMillis()==trunc2BOD(pointInTime));
    
    DateWrapper d4= new DateWrapper();
    d4.setTimeInMillis(pointInTime);
    assertTrue("setTimeInMillis",d1.compareTo(d4)==0);
    

    TimeStruct t=millis2DateTimeStruct(pointInTime).time;
    DateWrapper d5= new DateWrapper();
    d5.setTime(t);
    Date dd=d5.getDate();
    
    System.out.println(d5.getTimeInMillis());
    System.out.println(pointInTime);
    assertTrue("setTime",trunc2Frac(pointInTime)==d5.getTimeInMillis());

}

/**
 * Test the following:
 * public static long convertToMillis(DateStruct aDate)
 * public static long convertToMillis(DateTimeStruct aDateTime)
 * public static long convertToMillis(TimeStruct aTime)
 * 
 */
public void testConvertToMillis(){
    //setTargetTimeToRightBeforeEOD();
    long m1=DateWrapper.convertToMillis(millis2DateTimeStruct(pointInTime).date);
    long mx=trunc2BOD(pointInTime);
    assertTrue("convertToMillis(DateStruct aDate)",m1==mx);
    
    DateTimeStruct dt=millis2DateTimeStruct(pointInTime);
    long m2=DateWrapper.convertToMillis(dt);
    assertTrue("convertToMillis(DateTimeStruct aDateTime)",m2==trunc2Frac(pointInTime));
    
    TimeStruct t=millis2DateTimeStruct(pointInTime).time;
    long m3=DateWrapper.convertToMillis(t);
    
    //System.out.println(m3+" "+ trunc2Frac(pointInTime));
    Date d1= new Date(m3);
    Date d2= new Date(trunc2Frac(pointInTime));
    //System.out.println("d1="+d1);
    //System.out.println("d2="+d2);
    assertTrue("convertToMillis(TimeStruct aTime)",m3==trunc2Frac(pointInTime));
}

/**
 * Test the following:
 * public static DateStruct convertToDate(long aTime)
 * public static DateTimeStruct convertToDateTime(long aTime)
 * public static TimeStruct convertToTime(long aTime)
 */
public void testConvertToStructs(){
    setTargetTimeToRightAfterEOD();
    DateTimeStruct dt=millis2DateTimeStruct(pointInTime);
    DateStruct d1=DateWrapper.convertToDate(pointInTime);
    assertTrue("convertToDate",
            dt.date.day==d1.day
            &&dt.date.month==d1.month
            &&dt.date.year==d1.year);
    
    TimeStruct t1=DateWrapper.convertToTime(pointInTime);
    assertTrue("convertToTime",
            dt.time.hour==t1.hour
            &&dt.time.minute==t1.minute
            &&dt.time.second==t1.second
            &&dt.time.fraction==t1.fraction);
    
    DateTimeStruct dt1=DateWrapper.convertToDateTime(pointInTime);
    
    assertTrue("convertToDateTime",
            dt.date.day==dt1.date.day
            &&dt.date.month==dt1.date.month
            &&dt.date.year==dt1.date.year
            &&dt.time.hour==dt1.time.hour
            &&dt.time.minute==dt1.time.minute
            &&dt.time.second==dt1.time.second
            &&dt.time.fraction==dt1.time.fraction);
}


private long trunc2BOD(long millis){
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(millis);
    int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
    int year = cal.get(Calendar.YEAR);
    cal.clear();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.DAY_OF_YEAR, dayOfYear); 
    return cal.getTimeInMillis();
}

private long trunc2Frac(long millis){

    return millis/10*10;
}


private Date millis2Date(long millis){
    return new Date(millis);
}

private Calendar millis2Calendar(long millis){
    Calendar c=Calendar.getInstance();
    c.setTimeInMillis(millis);
    return c;
}

// independent of DateWrapper mechanism
private DateTimeStruct millis2DateTimeStruct(long millis){
    Calendar c=Calendar.getInstance();
    c.setTimeInMillis(millis);
    DateStruct d= new DateStruct();
    d.year=(short)c.get(Calendar.YEAR);
    d.month=(byte)(c.get(Calendar.MONTH)+1);
    d.day=(byte)c.get(Calendar.DATE);
    TimeStruct t = new TimeStruct();
    t.hour = (byte) c.get(Calendar.HOUR_OF_DAY);
    t.minute = (byte) c.get(Calendar.MINUTE);
    t.second = (byte) c.get(Calendar.SECOND);
    t.fraction=(byte)(( millis%1000)/10);
    return new DateTimeStruct(d,t);
}

} 