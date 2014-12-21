// -----------------------------------------------------------------------------------
// Source file: CalendarServiceTypes.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

/**
 * Formats a CalendarServiceType
 */
public class CalendarServiceTypes
{
    public static final int HOLIDAY       = com.cboe.infraUtil.DayTypes.HOLIDAY;
    public static final int NONSETTLEMENT = com.cboe.infraUtil.DayTypes.NONSETTLEMENTDAY;
    public static final int WEEKEND       = com.cboe.infraUtil.DayTypes.WEEKEND;

    public static final String WEEKEND_STRING = "Weekend";
    public static final String HOLIDAY_STRING = "Holiday";
    public static final String NON_SETTLEMENT_STRING = "Non Settlement";
    public static final String UNKNOWN_CALENDAR_TYPE_STRING = "Unknown Calendar Type";

    public static final int[] allTypes = {HOLIDAY, NONSETTLEMENT, WEEKEND};

    private CalendarServiceTypes()
    {
    }

    /**
     * Formats a calendar service type
     * @param calendar service type to format
     */
    public static String toString(int calendarServiceType)
    {
        String retVal = "";
        switch(calendarServiceType)
        {
            case WEEKEND:
                retVal = WEEKEND_STRING;
                break;
            case HOLIDAY:
                retVal = HOLIDAY_STRING;
                break;
            case NONSETTLEMENT:
                retVal = NON_SETTLEMENT_STRING;
                break;
            default:
                retVal = new StringBuffer(35).append(UNKNOWN_CALENDAR_TYPE_STRING).append(" ").append(calendarServiceType).toString();
                break;
        }
        return retVal;
    }

    /**
     * Get a list of all the types
     */
    public static int[] getAllTypes()
    {
        return allTypes;    
    }
}
