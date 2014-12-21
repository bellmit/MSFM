package com.cboe.interfaces.domain;

import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiOrder.AuctionStruct;

/**
 * The interface provides domain auction interface
 */
public interface Auction {

    public static final short AIM_AUCTION = 1;
    public static final short HAL_AUCTION = 2;
    
    public static final short AUCTION_INFO_NON_HAL = 0;
    public static final short AUCTION_INFO_NBBO_REJECT = 1;
    public static final short AUCTION_INFO_TWEENER_LOCK = 2;
    public static final short AUCTION_INFO_TWEENER = 3;
    public static final short AUCTION_INFO_UNSPECIFIED_AIM = 4;
    public static final short AUCTION_INFO_GUARANTEE_STARTING_PRICE = 5;
    public static final short AUCTION_INFO_LIMIT_PRICE = 6;
    public static final short AUCTION_INFO_AUTO_MATCH = 7;

    
    // Attributes in AuctionStruct
    public CboeIdStruct getAuctionId();
    public String getSession();
    public int getClassKey();
    public int getProductKey();
    public short getProductType();

    //the auctioned order side
    public Side getSide();

    // the total auctioned quantity
    public int getAuctionQuantity();

    // the starting price of the auction
    public Price getStartingPrice();
    public short getAuctionedOrderContingencyType();

    // return time when the auction start
    public long getStartTime();

    // return time when the auction really expires
    public long getExpireTime();

    // return expected auction end time
    public long getEndTime();

    public int getTimeToLive(); // ToDo: any different from (getEndTime() - getStartTime())?

    public short getAuctionType();
    
    public short getAuctionState();

    public short getAuctionTerminatedReason(); // ToDo: check the constants, if needs to be expose to external world?

    public String getExtensions();

    public Price getOppSideBotrPrice();

    public int getOppSideBotrQuantity();

    public Price getOppSideCboePrice();
    
    public int getOppSideCboeQuantity();

    public String getOrsId();
    
    public short getAuctionInfo();

    // return the auctionedOrder
    public Order getAuctionedOrder();


    public AuctionStruct getAuctionStruct();

    // return a boolean to indicate if the auciton is still active or expired
    public boolean isActive();

    /**
     * get UniqueId - databaseID
     * @return
     */
    public long getUniqueId();

    // set methods

    public void setAuctionInfo(short aValue);

    /**
     * @param quantity -  int, the total quantity to be auctioned
     */
    public void setAuctionQuantity(int quantity);

    public void setClassKey(int aValue);

    /**
     *
     * @param expireTime - long, the time when the auctionTimer is scheduled by TimerService
     * or the auction premature time
     */
    public void setExpireTime(long expireTime);

    /**
     * @param endTime - long, the auction end timestamp before the whole transaction is committed
     */
    public void setEndTime(long endTime);

    public void setTimeToLive(int theTimeToLive);

    public void setOppSideBotrPrice(Price aValue);

    public void setOppSideBotrQuantity(int aValue);

    public void setOppSideCboePrice(Price aValue);
    
    public void setOppSideCboeQuantity(int aValue);
    
    public void setOrsId(String aValue);
    
    public void setSide(char aValue);
    
    /**
     * set the auction terminate reason
     * @param reasonCode
     */
    public void setAuctionTerminatedReason(short reasonCode);
    
    public void setDerivedQuoteWhenAuctionStarted(DerivedQuote quote);
    
    public DerivedQuote getDerivedQuoteWhenAuctionStarted();

}
