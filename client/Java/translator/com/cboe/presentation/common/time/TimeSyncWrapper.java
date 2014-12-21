package com.cboe.presentation.common.time;

import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.domain.util.DateWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeSyncWrapper
{
    protected static long offset = 0;

    private TimeSyncWrapper()
    {
        super();
    }

    static public long getTimeOffset()
    {
        return offset;
    }

    static protected void setTimeOffset(long anOffset)
    {
        offset = anOffset;
    }

    static public void calculateTimeOffset(HeartBeatStruct aHeartBeatStruct)
    {
        DateTimeStruct aDateTimeStruct = new  DateTimeStruct(aHeartBeatStruct.currentDate, aHeartBeatStruct.currentTime);
        long heartBeatMillis = DateWrapper.convertToMillis(aDateTimeStruct);
        long localMillis = System.currentTimeMillis();

        setTimeOffset(heartBeatMillis - localMillis);
//        GUILoggerHome.find().information("in calculateTimeOffset() heartbeat time = "+heartBeatMillis);
//        GUILoggerHome.find().information("in calculateTimeOffset() system time = "+System.currentTimeMillis());
//        GUILoggerHome.find().information("in calculateTimeOffset() offset = "+getTimeOffset());
//        GUILoggerHome.find().information("in calculateTimeOffset() corrected time ="+getCorrectedTimeMillis());



    }

    static public long getCorrectedTimeMillis()
    {
//        GUILoggerHome.find().information("in getCorrectedTimeMillis() system time = "+System.currentTimeMillis());
//        GUILoggerHome.find().information("in getCorrectedTimeMillis() offset = "+getTimeOffset());
//        GUILoggerHome.find().information("in getCorrectedTimeMillis() corrected time = "+(System.currentTimeMillis()+getTimeOffset()));
        return (System.currentTimeMillis() + getTimeOffset());

    }

    static public long getElapsedCorrectedTime(long time)
    {
        return time - getCorrectedTimeMillis();
    }

    static public long getElapsedTime(long time)
    {
        return (System.currentTimeMillis() + time) - getCorrectedTimeMillis();
    }

    public static String formatDateInBrackets()
    {
        SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm:ss");
        return "[" + formatter.format(new Date(getCorrectedTimeMillis())) + "] ";
    }


}