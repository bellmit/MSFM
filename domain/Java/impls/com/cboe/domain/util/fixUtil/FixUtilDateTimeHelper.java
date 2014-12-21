package com.cboe.domain.util.fixUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.TimeStruct;

/**
 * Helper class that provides static methods to convert to/from the
 * com.cboe.idl.cmiUtil.DateTime class
 * <br><br>
 * Copyright © 1999 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software program is made available to
 * CBOE members and member firms to enable them to develop software applications using
 * the CBOE Market Interface (CMi), and its use is subject to the terms and conditions
 * of a Software License Agreement that governs its use. This document is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 * @author Jim Northey
 *
 */
public class FixUtilDateTimeHelper {
  private static ThreadLocal<DecimalFormat> yearFormatRef = new ThreadLocal<DecimalFormat>()
  {
      protected DecimalFormat initialValue()
      {
          return new DecimalFormat("0000");
      }
  };
  private static ThreadLocal<DecimalFormat> monthDayFormatRef = new ThreadLocal<DecimalFormat>()
  {
      protected DecimalFormat initialValue()
      {
          return new DecimalFormat("00");
      }
  };
  private static ThreadLocal<DecimalFormat> hourMinuteSecondFormatRef = new ThreadLocal<DecimalFormat>()
  {
      protected DecimalFormat initialValue()
      {
          return new DecimalFormat("00");
      }
  };
  private static ThreadLocal<DecimalFormat> millisecondFormatRef = new ThreadLocal<DecimalFormat>()
  {
      protected DecimalFormat initialValue()
      {
          return new DecimalFormat("0000");
      }
  };
  private static ThreadLocal<GregorianCalendar> calendarRef = new ThreadLocal<GregorianCalendar>()
  {
      protected GregorianCalendar initialValue()
      {
          return new GregorianCalendar();
      }
  };

  public FixUtilDateTimeHelper() {
  }

  public static DateTimeStruct makeEmptyDateTimeStruct(){

      DateTimeStruct emptyDateTimeStruct = new DateTimeStruct();
      emptyDateTimeStruct.date = new DateStruct();
      emptyDateTimeStruct.date.year = 0;
      emptyDateTimeStruct.date.month = 0;
      emptyDateTimeStruct.date.day = 0;
      emptyDateTimeStruct.time = new TimeStruct();
      emptyDateTimeStruct.time.hour = 0;
      emptyDateTimeStruct.time.minute = 0;
      emptyDateTimeStruct.time.fraction = 0;
      return emptyDateTimeStruct;
  }

  public static DateTimeStruct makeDateTimeStruct(String strDate) throws Exception {

      StringTokenizer dateTimeParser = new StringTokenizer(strDate," ");

      int numFields = dateTimeParser.countTokens();

      boolean dateOnly = false;
      if ( numFields < 1 || numFields > 2) {
	 throw new ParseException("Invalid Date Time Format Provided to DateTimeHelper: "+strDate,0);
      }
      else if (numFields == 1 ) {
	 dateOnly = true;
      }
      else {
	 dateOnly = false;
      }

      DateTimeStruct aDateTimeStruct = new DateTimeStruct();

      aDateTimeStruct.date = FixUtilDateHelper.makeDateStruct(dateTimeParser.nextToken());

      if (dateOnly) {
	 aDateTimeStruct.time = new TimeStruct();
	 aDateTimeStruct.time.hour = 0;
	 aDateTimeStruct.time.minute = 0;
	 aDateTimeStruct.time.second = 0;
	 aDateTimeStruct.time.fraction = 0;
      }
      else {
	 aDateTimeStruct.time = FixUtilTimeHelper.makeTimeStruct(dateTimeParser.nextToken());
      }

      return aDateTimeStruct;
  }

  public static DateTimeStruct makeDateTimeStruct(java.util.Date aDate){

      GregorianCalendar cal = calendarRef.get();
      cal.setTime(aDate);
      return makeDateTimeStruct(cal);
  }

  public static DateTimeStruct makeDateTimeStruct(Calendar cal) {

      DateTimeStruct aDateTimeStruct = new DateTimeStruct();

      aDateTimeStruct.date = new DateStruct();

      aDateTimeStruct.time = new TimeStruct();

			aDateTimeStruct.date.year = (short) cal.get(Calendar.YEAR);

			// use 1 based month rather than 0 based month

			aDateTimeStruct.date.month = (byte) (cal.get(Calendar.MONTH) + 1);
			aDateTimeStruct.date.day = (byte) cal.get(Calendar.DATE);

      aDateTimeStruct.time.hour = (byte) cal.get(Calendar.HOUR_OF_DAY);
      aDateTimeStruct.time.minute = (byte) cal.get(Calendar.MINUTE);
      aDateTimeStruct.time.second = (byte) cal.get(Calendar.SECOND);
      aDateTimeStruct.time.fraction = (byte) (cal.get(Calendar.MILLISECOND) / 10);

      return aDateTimeStruct;
  }

  /**
   * Returns a date/time as a string suitable for a FIX UTC timestamp field
   * @param aDateTimeStruct a CMi date/time
   * @return a string formatted as a FIX UTC timestamp
   */
  public static String dateTimeStructToString(DateTimeStruct aDateTimeStruct) {
		Date date = dateTimeStructToDate(aDateTimeStruct);
		return FixUtilDateTimeFormatter.getFixTime(date);
	}


  public static java.util.Date dateTimeStructToDate(DateTimeStruct aDateTimeStruct ){

      GregorianCalendar gCal = dateTimeStructToGregorianCalendar(aDateTimeStruct);

      return gCal.getTime();
  }

  public static GregorianCalendar dateTimeStructToGregorianCalendar(DateTimeStruct aDateTimeStruct ){

      GregorianCalendar gCal = new GregorianCalendar();

      gCal.set(Calendar.YEAR,aDateTimeStruct.date.year);
      gCal.set(Calendar.MONTH, aDateTimeStruct.date.month - 1);
      gCal.set(Calendar.DAY_OF_MONTH,aDateTimeStruct.date.day);
      gCal.set(Calendar.HOUR_OF_DAY,aDateTimeStruct.time.hour);
      gCal.set(Calendar.MINUTE,aDateTimeStruct.time.minute);
      gCal.set(Calendar.SECOND,aDateTimeStruct.time.second);
      gCal.set(Calendar.MILLISECOND,aDateTimeStruct.time.fraction*10);

      return gCal;
  }

  public static String getCurrentYYYYMMDDHHNNSSmmmm() {
    GregorianCalendar gregCal = calendarRef.get();

    int year = gregCal.get(Calendar.YEAR);
    int month = gregCal.get(Calendar.MONTH) + 1;
    int day = gregCal.get(Calendar.DAY_OF_MONTH);
    int hour = gregCal.get(Calendar.HOUR_OF_DAY);
    int minute = gregCal.get(Calendar.MINUTE);
    int second = gregCal.get(Calendar.SECOND);
    int millis = gregCal.get(Calendar.MILLISECOND);

    DecimalFormat monthDayFormat = monthDayFormatRef.get();
    DecimalFormat hourMinuteSecondFormat = hourMinuteSecondFormatRef.get();

    String formattedString = yearFormatRef.get().format(year) +
			     monthDayFormat.format(month) +
			     monthDayFormat.format(day) +
			     hourMinuteSecondFormat.format(hour) +
			     hourMinuteSecondFormat.format(minute) +
			     hourMinuteSecondFormat.format(second) +
			     millisecondFormatRef.get().format(millis);

    return formattedString;
  }
}
