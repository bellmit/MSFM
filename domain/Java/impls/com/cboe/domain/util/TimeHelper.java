package com.cboe.domain.util;

import java.util.Calendar;

public abstract class TimeHelper
{
    private static final CalendarLocal calendar = new CalendarLocal();

    private TimeHelper() { }

    public static int getMillisSinceMidnight()
    {
        return getMillisSinceMidnight(System.currentTimeMillis());
    }

    public static int getMillisSinceMidnight(long millisSinceEpoch)
    {
        Calendar cal = (Calendar) calendar.get();
        cal.setTimeInMillis(millisSinceEpoch);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (millisSinceEpoch - cal.getTime().getTime());
    }
    
    private static class CalendarLocal extends ThreadLocal
    {
        protected Object initialValue()
        {
            return Calendar.getInstance();
        }
    }
}
