package com.cboe.interfaces.domain.tradingProperty;

public interface AllowedHALTypes extends TradingProperty
{
    public static final short NONE_HAL = 0;
    public static final short NBBO_REJECT = 1;
    public static final short TWEENER_LOCK = 2;
    public static final short TWEENER = 3;
//    public static final short HAL_REMAINING = 4;

    short getAllowedHALType();
    void setAllowedHALType(short halType);
}
