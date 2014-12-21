//
// -----------------------------------------------------------------------------------
// Source file: DateFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.text.DateFormat;
import java.util.*;

import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;

/**
 * Responsible for formatting dates
 */
public class DateFormatter extends Formatter implements DateFormatStrategy
{
    static private HashMap<String, DateFormatThreadLocal> dateFormatters = null;

    // the style names, description and date formats must be matched in the array indexes.
    static private String[] styleNames =
        {
            DATE_FORMAT_24_HOURS_STYLE,
            DATE_FORMAT_24_HOURS_REVERSE_STYLE,
            DATE_FORMAT_24_HOURS_TIME_FIRST_STYLE,
            DATE_FORMAT_12_HOURS_STYLE,
            TIME_FORMAT_24_HOURS_STYLE,
            TIME_FORMAT_12_HOURS_STYLE,
            DATE_FORMAT_SHORT_STYLE,
            DATE_FORMAT_MONTHYEAR_STYLE,
            TIME_FORMAT_24_HOURS_SECONDS_STYLE,
            DATE_TIME_SHORT_12_HOURS_STYLE,
            DATE_FORMAT_YEARMONTHDATE_STYLE,
            DATE_FORMAT_TIMER_EXPIRATION_STYLE,
            DATE_FORMAT_24_HOURS_MRKTHISTORY_STYLE,
            TIME_FORMAT_12_HOURS_SECONDS_STYLE,
            DATE_TIME_FORMAT_12_HOURS_SECONDS_STYLE,
            DATE_TIME_FORMAT_24_HOURS_SECONDS_STYLE,
            DATE_FORMAT_MONTH_DATE_YEAR_STYLE

        };

     static private String[] styleDesc =
        {
            DATE_FORMAT_24_HOURS_DESCRIPTION,
            DATE_FORMAT_24_HOURS_REVERSE_DESCRIPTION,
            DATE_FORMAT_24_HOURS_TIME_FIRST_DESCRIPTION,
            DATE_FORMAT_12_HOURS_DESCRIPTION,
            TIME_FORMAT_24_HOURS_DESCRIPTION,
            TIME_FORMAT_12_HOURS_DESCRIPTION,
            DATE_FORMAT_SHORT_DESCRIPTION,
            DATE_FORMAT_MONTHYEAR_DESCRIPTION,
            TIME_FORMAT_24_HOURS_SECONDS_DESCRIPTION,
            DATE_TIME_SHORT_12_HOURS_DESCRIPTION,
            DATE_FORMAT_YEARMONTHDATE_DESCRIPTION,
            DATE_FORMAT_TIMER_EXPIRATION_DESCRIPTION,
            DATE_FORMAT_24_HOURS_MRKTHISTORY_DESCRIPTION,
            TIME_FORMAT_12_HOURS_SECONDS_DESCRIPTION,
            DATE_TIME_FORMAT_12_HOURS_SECONDS_DESCRIPTION,
            DATE_TIME_FORMAT_24_HOURS_SECONDS_DESCRIPTION,
            DATE_FORMAT_MONTH_DATE_YEAR_DESCRIPTION
        };

     static private String[] dateFormats =
        {
            DATE_FORMAT_24_HOURS_SPEC,
            DATE_FORMAT_24_HOURS_REVERSE_SPEC,
            DATE_FORMAT_24_HOURS_TIME_FIRST_SPEC,
            DATE_FORMAT_12_HOURS_SPEC,
            TIME_FORMAT_24_HOURS_SPEC,
            TIME_FORMAT_12_HOURS_SPEC,
            DATE_FORMAT_SHORT_SPEC,
            DATE_FORMAT_MONTHYEAR_SPEC,
            TIME_FORMAT_24_HOURS_SECONDS_SPEC,
            DATE_TIME_SHORT_12_HOURS_SPEC,
            DATE_FORMAT_YEARMONTHDATE_SPEC,
            DATE_FORMAT_TIMER_EXPIRATION_SPEC,
            DATE_FORMAT_24_HOURS_MRKTHISTORY_SPEC,
            TIME_FORMAT_12_HOURS_SECONDS_SPEC,
            DATE_TIME_FORMAT_12_HOURS_SECONDS_SPEC,
            DATE_TIME_FORMAT_24_HOURS_SECONDS_SPEC,
            DATE_FORMAT_MONTH_DATE_YEAR_SPEC
        };

    /**
     * Default constructor
     */
    public DateFormatter()
    {
        super();
        initialize();
    }

    /* this should only need to be called once when the class is created and not per instance */
    protected void initializeMaps()
    {
        dateFormatters = new HashMap<String, DateFormatThreadLocal>();
        for(int i=0; i<styleNames.length; i++)
        {
            DateFormatThreadLocal dateFormat = new DateFormatThreadLocal(dateFormats[i]);
            dateFormatters.put(styleNames[i],dateFormat);
        }

    }

    protected void initialize()
    {
        synchronized (this)
        {
            if(dateFormatters == null)
            {
                /* this should only need to be called once when the class is created and not per instance */

                initializeMaps();
            }
        }

        for(int i=0; i<styleNames.length; i++)
        {
            addStyle(styleNames[i], styleDesc[i]);
        }
        setDefaultStyle(DEFAULT_FORMAT_STYLE);

    }

    /**
     * Formats a date according to the currently selected style.
     * @param date a java.util.Date
     * @return a formatted String, or an empty string if date is null
     */
    public String format(java.util.Date date)
    {
        return format(date, getDefaultStyle());
    }

    /**
     * Defines a method for formatting a date according to the specified style
     * @param date <code>Date</code> to format
     * @param style the style
     * @return formatted string, or empty string if date is null or style is invalid.
     */
    public String format(java.util.Date date, String style)
    {
        return getDateFormat(style).format(date);
    }

    /**
     * Defines a method for formatting dates.
     * @param date to format.
     * @return formatted string, or empty string if date is null.
     */
    public String format(Date date)
    {
        return format(date.getDate());
    }

    /**
     * Defines a method for formatting a date according to the specified style
     * @param date to format
     * @param style the style
     * @return formatted string, or empty string if date is null or style is invalid.
     */
    public String format(Date date, String style)
    {
        return format(date.getDate(), style);
    }

    /**
     * Defines a method for formatting dates.
     * @param date to format.
     * @return formatted string, or empty string if date is null.
     */
    public String format(DateTime date)
    {
        return format(date.getCalendar().getTime());
    }

    /**
     * Defines a method for formatting a date according to the specified style
     * @param date to format
     * @param style the style
     * @return formatted string, or empty string if date is null or style is invalid.
     */
    public String format(DateTime date, String style)
    {
        return format(date.getCalendar().getTime(), style);
    }

    /**
     * Obtains the DateFormat object that implements the specified style.
     * @param style the style.  It must be a valid style, or an IllegalArgumentException will be thrown
     * @return the <code>DateFormat</code> that implements the style
     * @throws IllegalArgumentException
     */
    public DateFormat getDateFormat(String style)
           throws IllegalArgumentException
    {
        validateStyle(style);
        return dateFormatters.get(style).get();
    }
    /**
     * Obtains the DateFormat object for the current style.
     * @return the <code>DateFormat</code> that implements the style
     * @throws IllegalArgumentException It must be a valid style, or an IllegalArgumentException will be thrown
     */
    public DateFormat getDateFormat()
    {
        return getDateFormat(getDefaultStyle());
    }

    /**
     * Used for testing purposes.
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        DateFormatter dateFormatter1 = new DateFormatter();
        System.out.println(dateFormatter1.format(new java.util.Date()));
    }

    protected void validateStyle(String styleName)
    {
        super.validateStyle(styleName);
        if ( !dateFormatters.containsKey(styleName) )
        {
            throw new IllegalArgumentException("Invalid Date Format style : "+styleName);
        }
    }
}
