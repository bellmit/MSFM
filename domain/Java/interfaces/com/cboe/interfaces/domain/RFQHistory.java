package com.cboe.interfaces.domain;

/**
 * This interface defines the persistent Request For Quote History.
 *
 * @author Alex Torres
 */
public interface RFQHistory extends ActivityHistory
{
    public int getClassKey();
    public int getProductKey();
    public short getRfqType();
    public String getUserId();
    public long getQuantity();
    public long getTimeToLive();

    public void setClassKey (int anInteger);
    public void setProductKey (int anInteger);
    public void setRfqType (short aShort);
    public void setUserId (String aString);
    public void setQuantity (long aLong);
    public void setTimeToLive (long aLong);
    public void setEventTime (long aLong);
}
