package com.cboe.domain.tradingProperty;

import java.util.Comparator;

import com.cboe.domain.dateTime.TimeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.RegularMarketHours;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * @author josephg
 */
public class RegularMarketHoursImpl extends AbstractTradingProperty implements RegularMarketHours, Comparable
{
	public static final int MAX_TIME_IN_MILLIS_FROM_MIDNIGHT = 24 * 60 * 60 * 1000; // 1 ms extra
	public static final String PROPERTY_VALUE_DELIMETER = "-";
	private TimeStruct cachedOpenTimeStruct;
	private TimeStruct cachedCloseTimeStruct;
	private Time cachedOpenTime;
	private Time cachedCloseTime;
	
    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public RegularMarketHoursImpl(String sessionName, int classKey)
    {
        super(RegularMarketHoursGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the Property to initialize
     * the trading property data with.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public RegularMarketHoursImpl(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param openTime  opening time represented in time in millis from midnight 
     * @param closeTime closing time represented in time in millis from midnight 
     */
    public RegularMarketHoursImpl(String sessionName, int classKey, int openTime, int closeTime)
    {
        this(sessionName, classKey);
        setOpenTimeAsMillisSinceMidnight(openTime);
        setCloseTimeAsMillisSinceMidnight(closeTime);
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public RegularMarketHoursImpl(String sessionName, int classKey, String value)
    {
        super(RegularMarketHoursGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey, value);
    }

	@Override
	public int compareTo(Object object) {
        int result;
        long openTime2 = ((RegularMarketHours) object).getOpenTimeAsMillisSinceMidnight();
        long closeTime2 = ((RegularMarketHours) object).getCloseTimeAsMillisSinceMidnight();
        if (getOpenTimeAsMillisSinceMidnight() < openTime2)
        {
        	result = -1;
        }
        else if (getOpenTimeAsMillisSinceMidnight() > openTime2)
        {
        	result = 1;
        }
        else if (getCloseTimeAsMillisSinceMidnight() < closeTime2)
        {
        	result = -1;
        }
        else if (getCloseTimeAsMillisSinceMidnight() > closeTime2)
        {
        	result = 1;
        }
        else
        {
        	result = 0;
        }
        return result;
	}

	@Override
    public int hashCode()
    {
        return getOpenTimeAsMillisSinceMidnight() + getCloseTimeAsMillisSinceMidnight();
    }

	@Override
	public TradingPropertyType getTradingPropertyType() {
		return RegularMarketHoursGroup.TRADING_PROPERTY_TYPE;
	}

	/*
	 * If the time string contains ":", the time is of the format HH:MM:SS;
	 * else time is already in timeInMillis from midnight. 
	 * Caller should catch the Exception and handle it appropriately as it can throw a few based on the input passed in.
	 */
	public static int parseTimeInMillis(String strTime) throws Exception
	{
        String[]timeToken = strTime.split(":");
        byte hr=0, min=0, sec=0;
        int time=-1;
        if (timeToken.length > 1)
        {
            hr = Byte.parseByte(timeToken[0]);
        	min = Byte.parseByte(timeToken[1]);
        	sec = Byte.parseByte(timeToken[2]);
        	time = (hr * 3600 + min * 60 + sec) * 1000;
        }
        else
        {
        	time = Integer.parseInt(strTime);
        }
        if (time < 0 || time >= MAX_TIME_IN_MILLIS_FROM_MIDNIGHT)
        {
        	throw new Exception("Invalid time specified!");
        }
		return time;
	}
   
	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.domain.tradingProperty.RegularMarketHours#getCloseTime()
	 */
	public int getCloseTimeAsMillisSinceMidnight() {
		
		return getInteger2();
	}

	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.domain.tradingProperty.RegularMarketHours#getOpenTime()
	 */
	public int getOpenTimeAsMillisSinceMidnight() {
		
		return getInteger1();
	}

	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.domain.tradingProperty.RegularMarketHours#setCloseTime(int)
	 */
	public void setCloseTimeAsMillisSinceMidnight(int closeTime) {
		setInteger2(closeTime);		
        clearCachedTimeStructs();
	}

	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.domain.tradingProperty.RegularMarketHours#setOpenTime(int)
	 */
	public void setOpenTimeAsMillisSinceMidnight(int openTime) {
		setInteger1(openTime);		
	}

    /**
     * Gets the open time as TimeStruct
     */
    public TimeStruct getOpenTimeStruct()
    {
    	if (cachedOpenTimeStruct == null)
    	{
    		int timeInSec = getOpenTimeAsMillisSinceMidnight() / 1000;
    		byte hr = (byte)(timeInSec / 3600);
    		timeInSec = timeInSec % 3600;
    		byte min = (byte)(timeInSec / 60);
    		byte sec = (byte)(timeInSec % 60);
    		cachedOpenTimeStruct = new TimeStruct(hr, min, sec, (byte)0);
    	}
    	return cachedOpenTimeStruct;
    }

    /**
     * Sets the open time as TimeStruct
     */
    public void setOpenTimeStruct(TimeStruct openTime)
    {
    	setOpenTimeAsMillisSinceMidnight((openTime.hour*3600 + openTime.minute*60 + openTime.second)* 1000);
    }

    /**
     * Gets the close time as TimeStruct
     */
    public TimeStruct getCloseTimeStruct()
    {
    	if (cachedCloseTimeStruct == null)
    	{
    		int timeInSec = getCloseTimeAsMillisSinceMidnight() / 1000;
    		byte hr = (byte)(timeInSec / 3600);
    		timeInSec = timeInSec % 3600;
    		byte min = (byte)(timeInSec / 60);
    		byte sec = (byte)(timeInSec % 60);
    		cachedCloseTimeStruct = new TimeStruct(hr, min, sec, (byte)0);
    	}
    	return cachedCloseTimeStruct;    	    	
    }

    /**
     * Sets the close time as TimeStruct
     */
    public void setCloseTimeStruct(TimeStruct closeTime)
    {
    	setCloseTimeAsMillisSinceMidnight((closeTime.hour*3600 + closeTime.minute*60 + closeTime.second)* 1000);
        clearCachedTimeStructs();
    }

    /**
     * Gets the open time as Time
     */
    public Time getOpenTime()
    {
    	if (cachedOpenTime == null)
    	{
    		cachedOpenTime = new TimeImpl(getOpenTimeStruct());
    	}
    	return cachedOpenTime;    	
    }

    /**
     * Sets the open time as Time
     */
    public void setOpenTime(Time openTime)
    {
    	setOpenTimeAsMillisSinceMidnight((openTime.getHour()*3600 + openTime.getMinute()*60 + openTime.getSecond())* 1000);    	
    }

    /**
     * Gets the close time as Time
     */
    public Time getCloseTime()
    {
    	if (cachedCloseTime == null)
    	{
    		cachedCloseTime = new TimeImpl(getCloseTimeStruct());
    	}
    	return cachedCloseTime;
    }

    /**
     * Sets the close time as Time
     */
    public void setCloseTime(Time closeTime)
    {
    	setCloseTimeAsMillisSinceMidnight((closeTime.getHour()*3600 + closeTime.getMinute()*60 + closeTime.getSecond())* 1000);    	
    	clearCachedTimeStructs();
    }


	private void clearCachedTimeStructs()
	{
		cachedOpenTimeStruct = null;
		cachedOpenTimeStruct = null;
		cachedOpenTime = null;
		cachedOpenTime = null;
	}
	
	@Override
    public String toString()
	{
		StringBuilder sb = new StringBuilder(50);
		TimeStruct timeStruct = getOpenTimeStruct();
		sb.append(" OpenTime: ");
		sb.append(timeStruct.hour);
		sb.append(":");
		sb.append(timeStruct.minute);
		sb.append(":");
		sb.append(timeStruct.second);
		sb.append(" - CloseTime: ");		
		timeStruct = getCloseTimeStruct();
		sb.append(timeStruct.hour);
		sb.append(":");
		sb.append(timeStruct.minute);
		sb.append(":");
		sb.append(timeStruct.second);
		return sb.toString();
	}

	/**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"openTime", "closeTime"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
	
}
