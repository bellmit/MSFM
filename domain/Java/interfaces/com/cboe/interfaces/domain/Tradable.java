package com.cboe.interfaces.domain;

import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiOrder.*;
import com.cboe.exceptions.*;
import com.cboe.idl.orderBook.TradableStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderBookStruct;

/**
 * This class was created in VisualAge.
 * @author Werner Kubitsch
 * @author Eric Fredericks
 */
public interface Tradable {
       public void acceptedByBook(long bookedTime, Price bookedPrice, boolean silent) throws DataValidationException;
       public long getBookedTime();
       public void setBookedTime(long newValue);
       public void resetBookedTime();
       public int getBookedStatus();
       public void setBookedStatus(int newStatus);
       public void cancel(short cancelReason) throws DataValidationException;
       public FilledReportStruct fill(int product, long tradeId, int quantity, Price aPrice, ContraPartyStruct[] contras, short tradeContext, char billingType, boolean updateTradable, TradableSnapShot captuerdFillData) throws DataValidationException;
       public void publishFilledReports(FilledReportStruct[] reports, String salePrefix, boolean updateTradable, TradableSnapShot captuerdFillData);
       //public void publishFilledReports(FilledReportStruct[] reports, String salePrefix, boolean updateTradable);
       public short getContingencyType();
       public ExchangeFirmStruct getFirm();
       public Price getInitialTradePrice();
       public int getMaxQuantity();
       public int getMinQuantity();

       public char getOrderOriginType();
       public Price getPrice();
       public Integer getProductKey();
       public int getQuantity();
       public Side getSide();
       public String getUserId();
       public ExchangeAcronymStruct getUserAcronym();
       public ExchangeAcronymStruct getUserAcronym(short tradeContext);
       public boolean hasVolumeContingency();
       public boolean isBuySide();
       public boolean isSellSide();
       public boolean isAsDefinedSide();
       public boolean isOppositeSide();
       public boolean isReinstatable();
       public void setInitialTradePrice(Price tradePrice);

       public boolean isBooked();   // check if indeed the tradable is in the OrderBook
       public boolean isBookedByAuction(); // check if indeed the tradable is booked in the AuctionBook
       public Price getLastBookedPrice();       
       public void setBookedByAuction(boolean booked);
       
       public void setContext(String contextName, Object value);
       public Object getContextValue(String contextName);
       
    /**
  * Method to populate TradeReportEntry data
  *
  * author Eric Fredericks
  * @param reportEntry An object implementing the TradeReportEntry interface
  */

  public void populateTradeReportEntryData(TradeReportEntry reportEntry, short tradeContext);
       public TradableStruct toTradableStruct();

    /**
    * Indicates if the tradable is eligible to be booked.
    *
    * author Connie Liang
    */
    public boolean eligibleForBook( );

    /**
    * Tells the tradable can now start to process its own logic
    * after the book is updated.
    *
    * author Connie Liang
    */
	public void postBookUpdateProcessing(OrderBook book) throws DataValidationException;

    /**
    * Tells the tradable can now book itself if it OKs it
    * @param book the order book to book in if appropriate
    * author Connie Liang
    */
	public void handleBookProcessing(OrderBook book) throws DataValidationException;

    /**
     * Return true if the tradable needs NBBO price protection
     */
    public boolean needsNBBOProtection();

    /**
     * @return boolean : if true it indicates the price of a tradable is only for
     *      the internal market only.
     *
     * Note: It will only have any effect when the price is better than the current market
     * published to the outside world. In this case, the new price will not be published to
     * the outside world if it returns true. If the price is equal to or worse than the
     * published current market to the outside world, it has no effect.
     *
     */
    public boolean isPriceOnlyForInternalMarket();

    /**
     * Return the quantity which is allowed to trade.
     *
     * Note: getQuantity() should return the total available quantity of a tradable.
     *      but in a particular trade, we may only want to trade with a partial of
     *      the total available quantity, which is specified by the result of getQuantityAllowed()
     */
    public int getQuantityAllowed();

    /**
     * Unfortunately this is specific to a particular order contingency (RESERVE),
     * but in general it is the quantity that can be displayed to the market.  In
     * all cases except RESERVE orders it is the QuantityAllowed.  For reserve
     * orders it may be less.
     *
     */
    public int getDisplayQuantity();
    
    public void savePreviousReserve();

    /**
     * Return a boolean to indicate if the tradable is involved in a quote trigger
     */
    public boolean isInvolvedInQuoteTrigger();

    /**
     * set involved in quote trigger flag
     *
     */
    public void setInvolvedInQuoteTrigger(boolean aBoolean);

    /**
     * adjusts the pending trade quantity
     */
    public void fillPendingTrade(int product, long tradeId, int quantity, Price aPrice, char billingType) throws DataValidationException;
  //  public void fillPendingTrade(int product, long tradeId, int quantity, Price aPrice) throws DataValidationException;

    /**
     * Fill a pending trade. From a tradable's point of view, a pending trade is a trade, in which
     *
     * 1. the trade price is known
     * 2. the quantity allocated to the tradable is also known
     * 3. but the contra parties to the tradable is not known.
     *
     * So when filling a pending trade, the tradable does the following
     *
     */
    public FilledReportStruct completePendingTrade(int product, long tradeId, int quantity, Price aPrice, ContraPartyStruct[] contraParties, char billingType) throws DataValidationException;


     /**
      * set the widen price
      */
     public void setWidenPrice(Price aPrice);

     /**
      * if the price has been widened
      */
     public boolean isWidened();

     /**
      * if this tradable should be treated like a Q-order based on its origin
      * comparing with the list of configured origin codes that should be treated as Q-order
      */
     public boolean treatedLikeQuote();

    /**
     * check if the order is an auction response order
     * @return true if and only if the order is an auction response
     */
    public boolean isAuctionResponse();

    /**
     * check if the order is a flash response order
     * @return true if and only if the order is a flash response
     */
    public boolean isNBBOFlashResponse();
    
     /**
      * Determines if the quantity available to trade is less than
      * the lot size for the product.
      *
      */
     public boolean isOddLot();
     
     /**
      * Determines if the quantity available to trade is greater than
      * the lot size and the remainder of dividing it by the lotSize is
      * greater than zero.
      *
      */
     public boolean isMixedLot();

     /**
      *  Returns the portion of the avaialble quantity that
      *  is evenly divisible by the lot size for the product.
      *
      */
     public int getRoundLotQuantity();

     /**
      *  Returns the portion of the available quantity that
      *  is not evenly divisible by the lot size for the product.
      *
      */
     public int getOddLotQuantity();

     /**
      * if this tradable should be treated like a customer order based on its origin
      * comparing with the list of configured origin codes that should be treated as customer order
      */
     public boolean treatedLikeCustomer();
     
     /**
      *  If the tradable is allowed to generate NBBOFlash based on its originType
      * @return boolean
      */
     public boolean generatesNBBOFlash();

    /**
     * if this tradable should be cleared like a Q-order based on its origin
     *
     */
    public boolean clearsLikeQuote();

    /**
     * The user associated with a Tradable is normally the submitter of the Tradable. users can
     * submit orders for other users. Those other users are the originators of the User.
     * So getOriginator() is to return the originator of the tradable, which may the submitter itself
     * or not.
     */
    public ExchangeAcronymStruct getOriginatorAcronym();

    /**
     * The acronym to use for clearing
     * @return  ExchangeAcronymStruct
     */
    public ExchangeAcronymStruct getClearingAcronym();
    
    /**
     * The acronym to use for clearing
     * @return  ExchangeAcronymStruct
     */
    public ExchangeAcronymStruct getClearingAcronym(short tradeContext);
    
    public OrderBookStruct toOrderBookStruct();
    
    /**
     * Does the tradable have any kind of OPG Contingency
     * @return The boolean specifying if the tradable has the contingency or not.
     * author Sandip Chatterjee
     */
    public boolean hasOPGContingency();

    public boolean isTradable(short aTradableType);
    
    public void fill(FilledReportStruct[] remoteLegFills) throws DataValidationException;
    
    public void setCalculateQuantityAllowed(boolean allowed);
    
    public boolean isCalculateQuantityAllowed();
    
    public void setSweepTradePrice(Price p_sweepTradePrice);
    
    // acceptedByBook(long, Price, boolean) made this unnecessary
    // public void acceptedByBookWithoutPublish(long bookedTime, Price bookedPrice);
    
    // new booking impl should take care of this
    // public void resetToPreviousState();
    
	public void setBookedQuantity(int newQuantity);
    
    public boolean hasReservedQuantityDisplayed();
    
    public boolean isProtected();
    
    public int getCrossProductRatioQuantity();
    
    public boolean isArrivedPreAuction();
    
    public void setArrivedPreAuction(boolean preAuction);
    
    public String getActiveSession();
    
    public char getSideValue();
    
    public boolean isSellShortRuleViolated();
    
    public void setSellShortRuleViolated(boolean violatedRule);
    
    //  To temporarily store NBBO while processing a quote
    public void setTempNBBO(NBBOStruct nbbo);
    public NBBOStruct getTempNBBO();
    public boolean isIOCOrLikeIOC();

    
    // This method added for C2 Allocation Strategy. C2 mostly uses price time for allocation. But in some cases like HAL, where responses are added to orderbook, the auction response
    //lose priority to other booked tradables. Hence saving the auction response received time.
    public void setReceivedTimeForAuctionResponse( long time);
    
    public long getReceivedTimeForAuctionResponse();
    
    public void updateForAsyncCreateTrade(int productKey, int tradedQuantity, Price tradePrice) throws DataValidationException;
    public boolean isAsyncCreateTradePending();
    public void completeAsyncCreateTrade();
    public int getCanceledTotalVolume();
    public int getCanceledSessionVolume();
    public int getRemainingVoume();
    public int getRemainingFillVoume();
    public short getTradableState();
    public int getTradedTotalVolume();
    public int getTradedSessionVolume();
    public double getAverageTradedPrice();
    public double getSessionAverageTradedPrice();    
    public boolean isSkipUpdateForAsyncTrade();//set this to true if needed to skip the completeAsyncCreateTrade step
    public int getAsyncCreateTradePending();
} // interface Tradable
