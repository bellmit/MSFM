package com.cboe.domain.util.fixUtil;
/**
 * Helper class that provides static methods to convert to/from the
 * com.cboe.idl.cmiUtil.TimeStruct class
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
 */
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.GregorianCalendar;
import java.util.Calendar;
import com.cboe.idl.cmiUtil.TimeStruct;

public class FixUtilTimeHelper {

  private static ThreadLocal<GregorianCalendar> calendarRef = new ThreadLocal<GregorianCalendar>()
  {
      protected GregorianCalendar initialValue()
      {
          return new GregorianCalendar();
      }
  };

  public FixUtilTimeHelper() {
  }

  public static TimeStruct makeTimeStruct(String strTime) throws ParseException {

      StringTokenizer timeParser = new StringTokenizer(strTime,":.");

      int nfields = timeParser.countTokens();
      boolean hasFraction = false;
      if ( nfields < 3 || nfields > 4 ) {
         throw new ParseException("Invalid time format "+strTime,0);
      }
      else if ( nfields == 3 ) {
          hasFraction = false;
      }
      else if ( nfields == 4 ) {
          hasFraction = true;
      }

      TimeStruct aTimeStruct = new TimeStruct();

      try {
         aTimeStruct.hour =  (byte)Integer.parseInt(timeParser.nextToken());
         aTimeStruct.minute =  (byte)Integer.parseInt(timeParser.nextToken());
         aTimeStruct.second =    (byte)Integer.parseInt(timeParser.nextToken());
         if (hasFraction){
            aTimeStruct.fraction = (byte)Integer.parseInt(timeParser.nextToken().substring(0,2));
         }
         else{
            aTimeStruct.fraction = 0;
         }
      }
      catch(Exception e){
          throw new ParseException(e.getMessage(),0);
      }

      return aTimeStruct;
  }

  public static TimeStruct makeTimeStruct(java.util.Date aDate){
      GregorianCalendar gCal = calendarRef.get();
      gCal.setTime(aDate);
      return makeTimeStruct(gCal);
  }

  public static TimeStruct makeTimeStruct(Calendar cal) {

      TimeStruct aTimeStruct = new TimeStruct();

			aTimeStruct.hour =   (byte) cal.get(Calendar.HOUR);
			aTimeStruct.minute = (byte) cal.get(Calendar.MINUTE);
			aTimeStruct.second = (byte) cal.get(Calendar.SECOND);
      aTimeStruct.second = (byte) (cal.get(Calendar.MILLISECOND) / 10);

      return aTimeStruct;
  }

  public static TimeStruct makeTimeStruct(byte hour, byte minute, byte second, byte fraction) {

      TimeStruct aTimeStruct = new TimeStruct();

			aTimeStruct.hour =     (byte) hour;
			aTimeStruct.minute =   (byte) minute;
			aTimeStruct.second  =  (byte) second;
      aTimeStruct.fraction = (byte) fraction;

      return aTimeStruct;
  }

  public static String timeStructToString(TimeStruct aTimeStruct ){

      StringBuilder timeString = new StringBuilder(13); // HH:MM:SS.sss
      // HH:
      if (aTimeStruct.hour < 0) {
          aTimeStruct.hour = (byte) 0x0;  // no negative hours
      }
      if (aTimeStruct.hour < 10) {
         timeString.append('0');
      }
      timeString.append(aTimeStruct.hour);
      timeString.append(':');
      // MM:  
      if (aTimeStruct.minute < 10) {
         timeString.append('0');
      }
      timeString.append(aTimeStruct.minute);
      timeString.append(':');
      // SS.
      if (aTimeStruct.second < 10) {
         timeString.append('0');
      }
      timeString.append(aTimeStruct.second);
      timeString.append('.');
      // sss
      if (aTimeStruct.fraction < 100) {
         timeString.append('0');
      }
      if (aTimeStruct.fraction < 10) {
         timeString.append('0');
      }
      timeString.append(aTimeStruct.fraction);

      return timeString.toString();
  }


  public static java.util.Date timeStructToDate(TimeStruct aTimeStruct ){

     GregorianCalendar gDate = calendarRef.get();
     gDate.setTimeInMillis(System.currentTimeMillis()); // set correct Date
     gDate.set(Calendar.HOUR, aTimeStruct.hour);
     gDate.set(Calendar.MINUTE, aTimeStruct.minute);
     gDate.set(Calendar.SECOND , aTimeStruct.second);
     gDate.set(Calendar.MILLISECOND, aTimeStruct.fraction * 10);

     return gDate.getTime();
  }
}
