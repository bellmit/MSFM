package com.cboe.domain.util;
public interface AuctionTerminateReasons
{
    public static final short UNSPECIFIED = 0;
    public static final short ORDER_MARKETABLE_AGAINST_BOOK = 1;
    public static final short ORDER_MARKETABLE_AGAINST_AUCTION = 2;
    public static final short QUOTE_BID_LOCK = 3;
    public static final short QUOTE_ASK_LOCK = 4;
    public static final short QUOTE_BID_TRIGGER = 5;
    public static final short QUOTE_ASK_TRIGGER = 6;
    public static final short QUOTE_MARKETABLE_AGAINST_AUCTION = 7;
    public static final short Q_ORDER_LOCK = 8;
    public static final short Q_ORDER_TRIGGER = 9;
    public static final short Q_ORDER_MARKETABLE_AGAINST_AUCTION = 10;
    public static final short AUCTION_RESPONSE = 11;
    public static final short NEW_AUCTION = 12;
    public static final short AUCTIONED_ORDER_CANCEL = 13;
    public static final short PRODUCT_STATE_CHANGE = 14;
    public static final short QUOTE_PRICE_WORSEN = 15;
    public static final short QUOTE_QUANTITY_DECREASE = 16;
    public static final short Q_ORDER_QUANTITY_DECREASE = 17;
    public static final short SAME_SIDE_QUOTE_UPDATE_BETTER_PRICE = 18;
    public static final short SAME_SIDE_QUOTE_BETTER_PRICE = 19;
    public static final short SAME_SIDE_ORDER_BETTER_PRICE = 20;
    public static final short QUOTE_TRIGGER_STARTED = 21;
    public static final short ORDER_TRADED_FULLY = 22;
    public static final short BOTR_BETTER_THAN_FLASH_PRICE = 23;
    public static final short INSUFFICIENT_QUANTITY_TO_TRADE = 24;
    public static final short ORDER_MARKETABLE_AGAINST_AUCTION_RESPONSE = 25;
    public static final short MANUAL_QUOTE_RECEIVED = 26;
    public static final short MARKETABLE_AGAINST_MANUAL_QUOTE = 27;  
    public static final short LEG_MARKET_MOVED=28;
    public static final short OPPOSITE_SIDE_ORDER_BETTER_AUCTION = 29;
    public static final short AUCTION_ORDER_NOT_TRADABLE_NBBO = 30;
    public static final short INVERTED_LOCKED_MARKET = 31;
    public static final short LEG_CURRENT_MARKET_IMPROVED = 32;
    
}
