package com.cboe.infrastructureServices.calendarService;

import com.cboe.infraUtil.DateStruct;


public interface BusinessCalendar {


    /**
      * getNBusinessdaysAfter -- Returns the nth business day after the time that is set on the calendar
      * @param int n -- number of business days to skip
      * @param int[] dayTypeMask -- array of what infraUtil::DayTypeConstants::dayType to INCLUDE. 
      *                             For example to find 5 days after a HOLIDAY or a 
      *                             SETTLEMENTDAY, the dayTypeMask array should contain 
      *                             these two entries. If the array has "0" as first and 
      *                             only entry it implies find "n" business days after 
      *                             today.
      *                                                        
      * @return a DataObject which represents the nth business day
     */
    public DateStruct getNBusinessdaysAfter (int n, int[] daytypeMask);

    /**
      * getNBusinessdaysBefore -- Returns the nth business day before the time that is set on 
      *                   the calendar
      * @param int n -- number of business days to skip
      * @param int[] dayTypeMask -- array of what infraUtil::DayTypeConstants::dayType to 
      *                             INCLUDE. 
      *                             For example to find 5 days before a HOLIDAY or a 
      *                             SETTLEMENTDAY, the dayTypeMask array should contain 
      *                             these two entries. If the array has "0" as first and 
      *                             only entry it implies find "n" business days before 
      *                             today.
      *
      * @return a DataObject which represents the nth business day before the today as 
      *        represented by the calendar
     */

    public DateStruct getNBusinessdaysBefore(int n, int[] daytypeMask); 

    /**
      * isABusinessDay -- Returns false if Day passed in is on a WEEKEND or a HOLIDAY 
      *                   else true
      * @param DateStruct aDay - the particular Day
      *
      * @return true or false
     */
      
    public boolean isABusinessDay (DateStruct aDay); 

    /*
     * isOlderThan -- takes in a number and compares the day n business days prior to today and the day represented by the calendar
     * returns true if this is older.
     * @param int n --  number of business days to go back
     * @return true if the day represented by the Calendar is older
    */

    public boolean isOlderThan(int n);

    /*
     * isThisABusinessDay -- returns true if the day represented by the calendar is a businessday
     * @return true is Calendar day is a business day
    */

    public boolean isThisABusinessDay();

    /**
      * isNotofType -- Returns false if type[s] passed in is not the same type as the 
      *                day the calendar is currently set to else true
      * @param int[] dayTypes -- array of what infraUtil::DayTypeConstants::dayType to 
      *                          INCLUDE. 
      *
      * @return true or false
     */
    public boolean isNotofType(int[] dayTypes); 

    /**
      * isNotofType -- Returns false if type[s] passed in is not the same type as the 
      *                day object passed in else true
      * 
      * @param int[] dayTypes -- array of what infraUtil::DayTypeConstants::dayType to 
      *                          INCLUDE. 
      * @param DateStruct aDay - the particular Day
      * @return true or false
     */
    public boolean isNotofType(DateStruct aDay, int[] dayTypes); 

    public void setTime(long time);
}
