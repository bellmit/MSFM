package com.cboe.interfaces.domain.tradingProperty;

import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.interfaces.domain.dateTime.Time;

/**
 * @author josephg
 */
public interface RegularMarketHours extends TradingProperty
{
    /**
     * Gets the open time as millis since midnight
     */
    int getOpenTimeAsMillisSinceMidnight();

    /**
     * Sets the open time as millis since midnight
     */
    void setOpenTimeAsMillisSinceMidnight(int openTime);

    /**
     * Gets the close time as millis since midnight
     */
    int getCloseTimeAsMillisSinceMidnight();

    /**
     * Sets the close time as millis since midnight
     */
    void setCloseTimeAsMillisSinceMidnight(int closeTime);

    /**
     * Gets the open time as TimeStruct
     */
    TimeStruct getOpenTimeStruct();

    /**
     * Sets the open time as TimeStruct
     */
    void setOpenTimeStruct(TimeStruct openTime);

    /**
     * Gets the close time as TimeStruct
     */
    TimeStruct getCloseTimeStruct();

    /**
     * Sets the close time as TimeStruct
     */
    void setCloseTimeStruct(TimeStruct closeTime);

    /**
     * Gets the open time as Time
     */
    Time getOpenTime();

    /**
     * Sets the open time as Time
     */
    void setOpenTime(Time openTime);

    /**
     * Gets the close time as Time
     */
    Time getCloseTime();

    /**
     * Sets the close time as Time
     */
    void setCloseTime(Time closeTime);

    
}
