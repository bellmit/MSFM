//
// -----------------------------------------------------------------------------------
// Source file: DateFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import java.text.DateFormat;

import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;

/**
 * Defines a contract for a class that provides Date formatting.
 */
public interface DateFormatStrategy extends FormatStrategy
{
    public static final String DATE_FORMAT_24_HOURS_STYLE       = "24 Hours Date";
    public static final String DATE_FORMAT_24_HOURS_REVERSE_STYLE = "24 Hours Date, Year First";
    public static final String DATE_FORMAT_24_HOURS_TIME_FIRST_STYLE = "24 Hours Date, Time First";
    public static final String DATE_FORMAT_12_HOURS_STYLE       = "12 Hours Date";
    public static final String DATE_FORMAT_SHORT_STYLE          = "Short Date";
    public static final String DATE_FORMAT_MONTHYEAR_STYLE      = "Month/Year";
    public static final String DATE_FORMAT_TIMER_EXPIRATION_STYLE = "Timer expiration";
    public static final String DATE_FORMAT_24_HOURS_DESCRIPTION = "24 Hours Date";
    public static final String DATE_FORMAT_24_HOURS_REVERSE_DESCRIPTION = "24 Hours Date, Year First";
    public static final String DATE_FORMAT_24_HOURS_TIME_FIRST_DESCRIPTION = "24 Hours Date, Time First";
    public static final String DATE_FORMAT_12_HOURS_DESCRIPTION = "12 Hours (AM/PM) Date";
    public static final String DATE_FORMAT_SHORT_DESCRIPTION    = "Short Date (no time)";
    public static final String DATE_FORMAT_MONTHYEAR_DESCRIPTION= "Month abbreviation and two digit year";
    public static final String DATE_FORMAT_TIMER_EXPIRATION_DESCRIPTION = "Linkage Timer Expiration";
    public static final String DATE_FORMAT_24_HOURS_SPEC        = "MM/dd/yyyy HH:mm:ss.S";
    public static final String DATE_FORMAT_24_HOURS_REVERSE_SPEC = "yyyy/MM/dd HH:mm:ss.S";
    public static final String DATE_FORMAT_24_HOURS_TIME_FIRST_SPEC = "HH:mm:ss.S MM/dd/yyyy";
    public static final String DATE_FORMAT_12_HOURS_SPEC        = "MM/dd/yyyy hh:mm:ss.S a";
    public static final String DATE_FORMAT_SHORT_SPEC           = "MM/dd/yyyy";
    public static final String DATE_FORMAT_MONTHYEAR_SPEC       = "MMMyy";
    public static final String DATE_FORMAT_TIMER_EXPIRATION_SPEC = "yyyyMMdd-HH:mm:ss.SSS"; // 20030925-15:24:05.700
    public static final String DATE_FORMAT_24_HOURS_MRKTHISTORY_STYLE = "24 Hours (AM/PM) Month/Day";
    public static final String DATE_FORMAT_24_HOURS_MRKTHISTORY_SPEC = "MM/dd HH:mm:ss.S a";
    public static final String DATE_FORMAT_24_HOURS_MRKTHISTORY_DESCRIPTION = "24 Hours (AM/PM) Month/Day";

    public static final String DATE_FORMAT_YEARMONTHDATE_STYLE  = "YearMonthDay Date";
    public static final String DATE_FORMAT_YEARMONTHDATE_DESCRIPTION = "Year Month Day Date no Time";
    public static final String DATE_FORMAT_YEARMONTHDATE_SPEC   = "yyyyMMdd";

    public static final String TIME_FORMAT_24_HOURS_STYLE       = "24 Hours Time";
    public static final String TIME_FORMAT_12_HOURS_STYLE       = "12 Hours AM/PM Time";
    public static final String TIME_FORMAT_24_HOURS_SECONDS_STYLE   = "24 Hours Time with Seconds";
    public static final String TIME_FORMAT_12_HOURS_SECONDS_STYLE   = "12 Hours AM/PM Time with Seconds";
    public static final String TIME_FORMAT_24_HOURS_DESCRIPTION = "24 Hours Time";
    public static final String TIME_FORMAT_12_HOURS_DESCRIPTION = "12 Hours AM/PM Time";
    public static final String TIME_FORMAT_24_HOURS_SECONDS_DESCRIPTION = "24 Hours Time with Seconds";
    public static final String TIME_FORMAT_12_HOURS_SECONDS_DESCRIPTION = "12 Hours AM/PM Time with Seconds";
    public static final String TIME_FORMAT_24_HOURS_SPEC        = "HH:mm";
    public static final String TIME_FORMAT_12_HOURS_SPEC        = "hh:mm a";
    public static final String TIME_FORMAT_24_HOURS_SECONDS_SPEC    = "HH:mm:ss.S";
    public static final String TIME_FORMAT_12_HOURS_SECONDS_SPEC    = "hh:mm:ss a";

    public static final String DEFAULT_FORMAT_STYLE             = DATE_FORMAT_24_HOURS_STYLE;
    public static final String DEFAULT_FORMAT_DESCRIPTION       = DATE_FORMAT_24_HOURS_DESCRIPTION;
    public static final String DEFAULT_FORMAT_SPEC              = DATE_FORMAT_24_HOURS_SPEC;

    public static final String DATE_TIME_SHORT_12_HOURS_SPEC = "MM/dd/yyyy hh:mm a";
    public static final String DATE_TIME_SHORT_12_HOURS_STYLE = "Date / 12 hour time";
    public static final String DATE_TIME_SHORT_12_HOURS_DESCRIPTION = "Date and 12 hour AM/PM";
    public static final String DATE_TIME_FORMAT_12_HOURS_SECONDS_SPEC = "MM/dd/yyyy hh:mm:ss a";
    public static final String DATE_TIME_FORMAT_12_HOURS_SECONDS_STYLE = "Date / 12 hour time with Seconds";
    public static final String DATE_TIME_FORMAT_12_HOURS_SECONDS_DESCRIPTION = "Date and 12 hour AM/PM with Seconds";
    public static final String DATE_TIME_FORMAT_24_HOURS_SECONDS_SPEC = "MM/dd/yyyy HH:mm:ss";
    public static final String DATE_TIME_FORMAT_24_HOURS_SECONDS_STYLE = "Date / 24 hour time with Seconds";
    public static final String DATE_TIME_FORMAT_24_HOURS_SECONDS_DESCRIPTION = "Date and 24 hour time with Seconds";

    public static final String DATE_FORMAT_MONTH_DATE_YEAR_SPEC = "MMM-dd-yy";
    public static final String DATE_FORMAT_MONTH_DATE_YEAR_STYLE = "MMM-dd-yy";
    public static final String DATE_FORMAT_MONTH_DATE_YEAR_DESCRIPTION = "MMM-dd-yy";

    /**
     * Defines a method for formatting dates.
     * @param date to format.
     * @return formatted string, or empty string if date is null.
     */
    public String format(java.util.Date date);

    /**
     * Defines a method for formatting a date according to the specified style
     * @param date to format
     * @param style the style
     * @return formatted string, or empty string if date is null or style is invalid.
     */
    public String format(java.util.Date date, String style);

    /**
     * Defines a method for formatting dates.
     * @param date to format.
     * @return formatted string, or empty string if date is null.
     */
    public String format(Date date);

    /**
     * Defines a method for formatting a date according to the specified style
     * @param date to format
     * @param style the style
     * @return formatted string, or empty string if date is null or style is invalid.
     */
    public String format(Date date, String style);

    /**
     * Defines a method for formatting dates.
     * @param date to format.
     * @return formatted string, or empty string if date is null.
     */
    public String format(DateTime date);

    /**
     * Defines a method for formatting a date according to the specified style
     * @param date to format
     * @param style the style
     * @return formatted string, or empty string if date is null or style is invalid.
     */
    public String format(DateTime date, String style);

    /**
     * Defines a method for obtaining a DateFormat object that is used by the specified style.
     * @param style the style
     * @return a <code>DateFormat</code> that can be used to format a date.
     */
    public DateFormat getDateFormat(String style);

    /**
     * Defines a method for obtaining the current style's DateFormat.
     * @return a <code>DateFormat</code> that can be used to format a date.
     */
    public DateFormat getDateFormat();
}