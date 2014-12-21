package com.cboe.ffUtil;

import com.cboe.ffidl.ffUtil.TimeStruct;
import java.util.Calendar;

public class TimeHelper
{
    public static TimeStruct createTimeStruct()
    {
        TimeStruct time = new TimeStruct();
        Calendar cal = Calendar.getInstance();
        time.hour = (byte)cal.get(Calendar.HOUR_OF_DAY);
        time.minute = (byte)cal.get(Calendar.MINUTE);
        time.second = (byte)cal.get(Calendar.SECOND);
        time.fraction = (byte)0;
        return time;
    }

    public static TimeStruct toStruct(int seconds)
    {
        TimeStruct time = new TimeStruct();
        time.fraction = 0;
        time.second = (byte)(seconds % 60);
        time.minute = (byte)((seconds/60) % 60);
        time.hour =  (byte)(seconds/3600);
        return time;
    }

    public static int toSeconds(TimeStruct time)
    {
        return  time.second + 60*(time.minute + 60*time.hour);
    }

    public static String toString(TimeStruct time)
    {
        StringBuffer buf = new StringBuffer(12);
        appendTwoDigit(buf, time.hour);
        buf.append(':');
        appendTwoDigit(buf, time.minute);
        buf.append(':');
        appendTwoDigit(buf, time.second);
        buf.append('.');
        buf.append(time.fraction);
        return buf.toString();
    }

    /**
     *  Read from a string in the precise format "hh:mm:ss" (length == 8) 
     */
    public static TimeStruct fromString(String timeStr)
        throws NumberFormatException
    {
        if (timeStr.length() != 8 || timeStr.charAt(2)!=':' || timeStr.charAt(5)!=':')
        {
            throw new NumberFormatException("Expected 8 digit string in format hh:mm:ss, got '" + timeStr + "'");
        }
        TimeStruct time = new TimeStruct();
        time.hour   = Byte.parseByte(timeStr.substring(0,2));
        time.minute = Byte.parseByte(timeStr.substring(3,5));
        time.second = Byte.parseByte(timeStr.substring(6,8));
        time.fraction = 0;
        return time;
    }

    protected static void appendTwoDigit(StringBuffer buf, int nbr)
    {
        if (nbr < 10 && nbr >= 0)
        {
            buf.append('0');
        }
        buf.append(nbr);
    }
}
