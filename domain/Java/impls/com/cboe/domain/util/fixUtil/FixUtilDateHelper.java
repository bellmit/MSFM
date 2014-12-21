package com.cboe.domain.util.fixUtil;


/**
 * Helper class that provides static methods to convert to/from the
 * com.cboe.idl.cmiUtil.Date class
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
import java.util.GregorianCalendar;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.StringTokenizer;
import com.cboe.idl.cmiUtil.DateStruct;

public class FixUtilDateHelper {

  /**
   * Decimal formatters used by methods
   */
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
  private static ThreadLocal<DecimalFormat> monthZDayFormatRef = new ThreadLocal<DecimalFormat>()
  {
      protected DecimalFormat initialValue()
      {
          return new DecimalFormat("#0");
      }
  };
  private static ThreadLocal<GregorianCalendar> calendarRef = new ThreadLocal<GregorianCalendar>()
  {
      protected GregorianCalendar initialValue()
      {
          return new GregorianCalendar();
      }
  };

     public FixUtilDateHelper() {
    }

    /**
   *
   *
   */
  public static DateStruct makeDateStruct(String strDate) throws ParseException {

      StringTokenizer dateParser = new StringTokenizer(strDate,"/");
      if ( dateParser.countTokens() < 3 ) {
         throw new ParseException("Invalid Date Format",0);
      }

      DateStruct aDateStruct = new DateStruct();

      try {
         aDateStruct.year = (short)Integer.parseInt(dateParser.nextToken());
         aDateStruct.month = (byte)Integer.parseInt(dateParser.nextToken());
         aDateStruct.day = (byte)Integer.parseInt(dateParser.nextToken());
      }
      catch (Exception e ){
         throw new ParseException(e.getMessage(),0);
      }
      return aDateStruct;
  }

  /**
   *
   *
   */
  public static DateStruct makeDateStruct(java.util.Date aDate){

      GregorianCalendar cal = calendarRef.get();
      cal.setTime(aDate);
      return makeDateStruct(cal);
  }

  /**
   *
   *
   */
  public static DateStruct makeDateStruct(Calendar cal) {

      DateStruct aDateStruct = new DateStruct();

			aDateStruct.year = (short) cal.get(Calendar.YEAR);

			// use 1 based month rather than 0 based month

			aDateStruct.month = (byte) (cal.get(Calendar.MONTH) + 1);
			aDateStruct.day = (byte) cal.get(Calendar.DATE);

      return aDateStruct;
  }

  /**
   *
   *
   *
   */
  public static DateStruct makeDateStruct(int year, int month, int day) {

      DateStruct aDateStruct = new DateStruct();

			aDateStruct.year = (short) year;

			// use 1 based month rather than 0 based month

			aDateStruct.month = (byte) month;
			aDateStruct.day = (byte) day;

      return aDateStruct;
  }


  /**
   *
   *
   */
  public static String dateStructToString(DateStruct aDateStruct ){

      StringBuilder result = new StringBuilder(20);
      result.append(aDateStruct.year).append('/').append(aDateStruct.month).append('/').append(aDateStruct.day);
      return result.toString();
  }

  /**
   *
   *
   */
  public static java.util.Date dateStructToDate(DateStruct aDateStruct ){

      GregorianCalendar gDate = calendarRef.get();
      gDate.set(aDateStruct.year, aDateStruct.month, aDateStruct.day, 0, 0, 0);
      return gDate.getTime();
  }

  /**
   *
   *
   */
  public static GregorianCalendar dateStructToGregorianCalendar(DateStruct aDateStruct ){
      GregorianCalendar gDate = new GregorianCalendar(aDateStruct.year,
                                         aDateStruct.month, aDateStruct.day);
      return gDate;
  }


  /**
   * Convert from a DateStruct to a string in the format of YYYYMM
   * Routine primarily used to support FIX MaturityMonthYear
   */
  public static String dateStructToYYYYMM(DateStruct aDateStruct ){

      return yearFormatRef.get().format(aDateStruct.year) +
             monthDayFormatRef.get().format(aDateStruct.month) ;

  }

  /**
   * Convert from a DateStruct - just returning the day of the month
   * Routine primarily used to support FIX MaturityDay
   */
  public static String dateStructToDD(DateStruct aDateStruct ){

      return monthDayFormatRef.get().format(aDateStruct.day) ;
  }

  /**
   * Convert from a DateStruct - just returning the day of the month
   * as 1 or 2 digits (1-31 as opposed to 01-31)
   * Routine primarily used to support FIX MaturityDay
   */
  public static String dateStructToZD(DateStruct aDateStruct ){

      return monthZDayFormatRef.get().format(aDateStruct.day) ;
  }

  /**
   * Returns the current YYYYMMDD as a string
   */

  public static String currentYYYYMMDD(){

      GregorianCalendar gCalendar = calendarRef.get();
      gCalendar.setTimeInMillis(System.currentTimeMillis());
      int year = gCalendar.get(Calendar.YEAR);
      int month = gCalendar.get(Calendar.MONTH);
      int day = gCalendar.get(Calendar.DAY_OF_MONTH);

      String yyyymmdd = yearFormatRef.get().format(year) +
                        monthDayFormatRef.get().format(month+1) +
                        monthDayFormatRef.get().format(day);
      return yyyymmdd;
  }
}
