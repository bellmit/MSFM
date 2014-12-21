package com.cboe.interfaces.domain;

import com.cboe.exceptions.DataValidationException;

import java.util.HashMap;
import java.util.List;

//IMPORTANT: This interface was originally defined in server. Now moved to domain due to
//the object is being used by TradingProduct (domain). Later on, AuctionInternal and Auction
//should be merged as there is not much gain having two, currently.
/**
 * The interface provides server auction interface. It extends domain auction interface.
 */
public interface AuctionInternal extends Auction {


    public static final int AUCTION_EXPIRE_TIMER_TYPE = 1;


    /**
     * Accept auction response
     */
    public void acceptAuctionResponse(Order order) throws DataValidationException;

    /**
     * Accept auction response cancel
     */
    public void acceptAuctionResponseCancel(Order order, int quantity, String userAssignedCancelId)
            throws DataValidationException;

    /**
     * Accept auction response cancel replace
     */
    public void acceptAuctionResponseCancelReplace(Order originalOrder, int quantity, Order newOrder, String userAssignedCancelId)
            throws DataValidationException;

   /**
    * expire and release the auction
    * Clean up all the related orders from the auction and clear the auction book associated with this auction,
    * set state to inactive, and call auctionHome.remove (theCurrentAuction) to remove it from the active collection, and so on.
    */
   public void expire() throws DataValidationException;

   /**
     * set Firm Match order for internlaization type of auctions
     * @param matchOrder
     */
    public void setFirmMatchOrder(Order matchOrder); // used by internalization only

    /**
     * This method will be used when auction type is an internalization auction
     * return the firm side match Order
     * otherwise, return null
     * @return
     */
    public Order getFirmMatchOrder();


    // publish the auction event to event channel with the default participants list
    public void publish();

    // publish the auction event to event channel, with the specified participants list
    public void publish(int[] participants);

    // set participants list of the auction
    public void setParticipants(int[] participants);

    // get participants list of the auction
    public int[] getParticipants();

    // start the auction, will create a broker auciton timer command and enqueue it.
    public void startAuction();

    // Join an existing auction
    public void joinAuction(Order joiningAuctionOrder, short auctionType, boolean addQuantity) throws DataValidationException;

    //returns a collection of all auctioned orders including original and joined orders
    public TradableList getAuctionedOrderList();

    //returns the total quantity of the auctioned orders.
    public int getTotalAuctionQuantity();

    //return the auction book this auction is associated with
    public AuctionBook getAuctionBook();

    // get the order which termincated the auction, if any
    public long getTerminateOrderId ();

    // set the order which termincated the auction
    public void setTerminateOrderId(long aValue);

    // get the quote which termincated the auction, if any
    public long getTerminateQuoteId ();

    // set the Quote which termincated the auction
    public void setTerminateQuoteId(long aValue);
 
    // get the quote key which terminated the auction, if any 
    public long getTerminateQuoteKey (); 
 
    // set the quote key which terminated the auction 
    public void setTerminateQuoteKey(long aValue); 
 
    // get the Quote User Id which terminated the auction, if any 
    public String getTerminateQuoteUserId (); 
 
    // set the Quote user Id which terminated the auction 
    public void setTerminateQuoteUserId(String aValue); 

    // get/set flash type for HAL auction
    public short getFlashType();
    public void setFlashType(short aFlashType);

    //mark the auction with trade price, this price will be calculated based on BOTR, limit, flash price
    public void setTradePrice(Price tradePriceIn);
    public Price getTradePrice();

    public void setAuctionInfoAim(short aValue);
    
    public short getAuctionStartingReason();
    public void setAuctionStartingReason(short aValue);   
    
    //set the original quotes sizes by use acronym, useful for SAL
    public void setCapDetails(HashMap capDetails);
    public HashMap getCapDetails();
    
    //Used for COmplex ORder PMM allocation.
    public void setPMMCapDetailsForRfpCOATrade(HashMap<String,Integer> capDetailsForPmmEntitlements);
    public HashMap<String,Integer> getPMMCapDetailsForRfpCOATrade();
    
    public void setPMMCapDetailsForCOATrade(HashMap<Integer,HashMap<String,Integer>> capDetailsForPmmEntitlements);
    public HashMap<Integer,HashMap<String,Integer>> getPMMCapDetailsForCOATrade();

    public void setCachedAuctionedOrdersList(List<Order> cachedAuctionedOrdersList);
    public List<Order> getCachedAuctionedOrdersList();
    
    public int getAuctionRemainingQuantity();

    public void setExtensions();
    public void setExtensions(String extensions);
    public String getExtensions();

    public void saveChangedDQ();
    public DerivedQuote getDQFromLastCMChange ();
   
}
