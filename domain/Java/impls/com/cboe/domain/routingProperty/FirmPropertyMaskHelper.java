package com.cboe.domain.routingProperty;

public class FirmPropertyMaskHelper
{
    public static final int[][] FIRM_TRADING_PARAM_MASK = {{0,0,0,0,1}, {0,0,0,1,1}};
    public static final int[][] PRICE_ADJUSTMENTS_CANCEL_PREFERENCE_MASK = {{0,1,1}};
    public static final int[][] FIRM_ETN_PARAM_MASK = {{0,1,1,0}};
    public static final int[][] DROP_COPY_PREFERENCE_MASK = {{0,1,1}};
    public static final int[][] MMTN_PREFERENCE_MASK = {{0,1,1,0}};
    public static final int[][] PAR_CLEARING_PREFERENCE_MASK = {{0,1,1,0}};
    public static final int[][] MMTN_MAP_PREFERENCE_MASK = {{0,0,0,1,0}};
    public static final int[][] MMTN_POST_STATION_MAP_PREFERENCE_MASK = {{0,0,0,0,1,0}, {0,0,0,1,1,0}};
    /**
     * defines the array for the auction firm info mask. 
     * @author Cognizant Technology Solutions.
     */
    public static final int[][] AUCTION_FIRM_INFO_MASK = {{1,1,1}};
    public static final int[][] DIRECTED_AIM_NOTIFICATION_FIRM_INFO_MASK = {{1,1,1}};
    public static final int[][] SHORT_SALE_MARKING_MASK = {{0,0,0},{0,1,1}};
    public static final int[][] ENALBE_NEW_BOB_MASK = {{0,1,1, 0}};
}
