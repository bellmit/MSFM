/*
 * Created on Oct 17, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.domain.util;

import java.util.Calendar;
import java.util.Date;

public class TimeSinceMidnightCalculator
{
    private static  Calendar  midNightCalendar;
    private static  Calendar  currentCalendar;
    
    
    private static void find()
    {
        if (midNightCalendar == null)
        {
            midNightCalendar = Calendar.getInstance();
            currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(midNightCalendar.getTimeInMillis()); //To make sure two calendar has same date
            midNightCalendar.set(Calendar.HOUR_OF_DAY,0);
            midNightCalendar.set(Calendar.MINUTE,0);
            midNightCalendar.set(Calendar.SECOND,0);
            midNightCalendar.set(Calendar.MILLISECOND,0);
        }
    }
    public static long getMilisecondsSinceMidnight(int hour,int minute,int second,int millisecond)
    {
        find();
        currentCalendar.set(Calendar.HOUR_OF_DAY,hour);
        currentCalendar.set(Calendar.MINUTE,minute);
        currentCalendar.set(Calendar.SECOND,second);
        currentCalendar.set(Calendar.MILLISECOND,millisecond);
        
        return currentCalendar.getTimeInMillis() - midNightCalendar.getTimeInMillis();
    }

    public static Date getTime(long timeSinceMidnight)
    {
        find();
        long time=midNightCalendar.getTimeInMillis()+timeSinceMidnight;
        currentCalendar.setTimeInMillis(time);
        return currentCalendar.getTime();
    }
    public static void main(String[] arg)
    {
        //this should print time exactly same as the input time
        System.out.println(getTime(getMilisecondsSinceMidnight(13,31,25,0)));
        System.out.println(getMilisecondsSinceMidnight(0,0,1,123));
    }
}
