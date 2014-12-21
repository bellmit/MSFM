package com.cboe.domain.util;

/**
 * The constants defined in SubActivityType will be used for subEventType of OrderHistory
 * to differentiate different eventType.
 */
public class SubActivityType
{
    public static final short DEFAULT = 0;
    public static final short HAL_ORDER_FILL_WITHIN_NBBO = 1;
    public static final short HAL_ORDER_FILL_OUTSIDE_NBBO = 2;
    public static final short SAL_AUCTION_TRADE = 3;
}
