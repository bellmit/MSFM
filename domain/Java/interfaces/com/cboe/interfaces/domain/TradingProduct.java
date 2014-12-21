package com.cboe.interfaces.domain;

import java.util.HashMap;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.OrderBookSummaryStruct;
import com.cboe.interfaces.domain.marketData.MarketData;

/**
 * A source of trading information about a product.
 *
 * @author John Wickberg
 */

public interface TradingProduct {

	/**
	 * Market status when order book is not complete.
	 */
	public static final int INCOMPLETE_MARKET = 1;

	
    /**
     * Market status when order book is crossed.
     */
    public static final int CROSSED_MARKET = 2;

    /**
     * Market status when order book is only one tick wide.
     */
    public static final int NARROW_MARKET = 3;

    /**
     * Market status when order book has a spread greater than one tick and less than or equal
     * to the exchange prescribed width.
     */
    public static final int NORMAL_MARKET = 4;

    /**
     *  Market status when order book exceeds the exchange prescribed width.
     */
    public static final int WIDE_MARKET = 5;

    /**
     *  Libality type.
     */
    public static final int CREDIT = 1;

    /**
     *  Libality type.
     */
    public static final int DEBIT = -1;

    
    public static final short NO_QUOTE_IS_AVAILABLE = 0;
    public static final short QUOTE_IS_AVAILABLE = 1;
    
    
	/**
	 * Adds a trading product listener.
	 *
	 * @param listener the listener to be added
	 */
	void addProductListener(TradingProductListener listener);

    /**
     * Notifies this product that the best quote of its order book has changed.
     */
    void bestBookUpdated();

    /**
     * Calculates settlement price for this product.
     *
     * @param previousSettlementPrice previous settlement price is used if product didn't trade today
     * @return new settlement price, may be NO_PRICE.
     */
    Price calculateSettlementPrice(PriceStruct previousSettlementPrice);

	/**
	 * Gets state code for current state.
	 *
	 * @return a product state code as defined in cmiConstants.idl
	 */
	short getCurrentStateCode();
	
	
	public boolean inNonTradingState() ;

	/**
	 * Gets derived quote for the product.  For spread products, the derived
	 * quote will represent the best price that can be obtained by trading
	 * the spread against the legs of the spread.  For other products, the
	 * derived quote is the same as the best book.
	 *
	 * @return derived quote for this product
	 * @exception DataValidationException if current state of legs prevents
	 *                                    calculation of a derived quote.
	 */
	DerivedQuote getDerivedQuote() throws DataValidationException, SystemException;
	
	DerivedQuote getDerivedQuote(BestPriceStruct[] definedSideLegMarkets, BestPriceStruct[] oppositeSideLegMarkets) throws DataValidationException, SystemException;
	
	/**
     *  Introduced as a part of the QCT Project. For QCT Trades, the BSM and the leg trade price calculations
 	 * should include the NBBO. Since the NewQuoteDerivedImpl is heavily tied up with useNBBO flag, we use
 	 * DerivedQuoteWithNBBO that will use NBBO by default for DQ calculations.
     * 
     * @return derived quote for this product
     * @exception DataValidationException if current state of legs prevents
     *                                    calculation of a derived quote.
     */
	DerivedQuote getDerivedQuoteWithNBBO() throws DataValidationException, SystemException;

	/**
     * Gets derived quote for the product.  For spread products, the derived
     * quote will represent the best price that can be obtained by trading
     * the spread against the legs of the spread. 
     * For Cross Product ,  derived quote will be calculated using cached NBBO data. 
     * 
     * @return derived quote for this product
     * @exception DataValidationException if current state of legs prevents
     *                                    calculation of a derived quote.
     */
	DerivedQuote getCachedDerivedQuote() throws DataValidationException, SystemException;

	/**
	 * Gets the exchange prescribed width based on a bid price.  The width
	 * will be adjusted if the class is in a fast market.
	 *
	 * @param basePrice base price for width lookup
	 * @return exchange prescribed width
	 */
	Price getExchangePrescribedWidth(Price basePrice);
    /**
	 * Gets the exchange prescribed width used during Opening based on a bid price.  The width
	 * will be adjusted if the class is in a fast market.
	 *
	 * @param basePrice base price for width lookup
	 * @return exchange prescribed width
	 */
    Price getOpeningExchangePrescribedWidth(Price basePrice);
	/**
	 * Gets the opening indicator, which represents if the product has opend at least once for the current session
	 * @return inital open
	 */
	boolean getInitialOpenOccurred();
    void setInitialOpenOccurred(boolean status);
    void setInitialOpenOccurredDb(boolean status);

	/**
	 * Gets the current market status for this product.
	 */
	int getMarketStatus();

    /**
     * Gets a unique sequence number for the trading product.  The numbers are unique by day and are always increasing.
     * There is no guarentee that the numbers are sequential.
     *
     */
	int getNextSequence();

    /**
     * Gets a current sequence number for the trading product generated previously
     * by  getNextSequence();
     *
     */
    int getCurrentSequence();


	/**
	 * Gets order book for this product.  A convenience method.
	 *
	 * @return this products order book
	 */
	OrderBook getOrderBook();

    Broker getBroker();
	/**
	 * Gets key of this product.
	 */
	int getProductKey();

	/**
	 * Gets product keys for this product.
	 */
	ProductKeysStruct getProductKeys();

    /**
     * Gets the product name struct for this product.
     */
    ProductNameStruct getProductNameStruct();

    /**
     * Gets the minimum quantity that must be met for a quote to be standard.
     */
    int getStandardQuoteSize();

	/**
	 * Gets the legs of a strategy.
	 *
	 * @return legs of strategy or empty sequence if product is not a strategy.
	 */
	TradingStrategyLeg[] getStrategyLegs();

	/**
	 * Gets state code for target state.  Target state may be different from
	 * current state if a state change is in progress.
	 *
	 * @return a product state code as defined in cmiConstants.idl
	 */
	short getTargetStateCode();
    
    /**
     * sets state code for target state.  Target state may be different from
     * current state if a state change is in progress. Use for distributed cache
     *
     * @return a product state code as defined in cmiConstants.idl
     */
    void setTargetStateCode(short newCode);
    

    /**
	 * Gets prod_sub_type_code stored in trading_prod table
	 * Defines the type of strategy order.
	 *
	 * @return a product sub type as defined in cmiConstants.idl
	 */
	short getProdSubTypeCode();

	/**
	 * Gets the trading class of this product.
	 */
	TradingClass getTradingClass();

    /**
     * Getters and setters for price field.
     */
	void setLastTradePrice(Price aValue, boolean notifyListeners);
	
    Price getLastTradePrice();

    /**
     * getMarketOrderTradePrice
     *
     * Returns the price for a market order trade (market orders on both sides of the trade).
     *
     * @return marketOrderTradePrice
     */
     Price getMarketOrderTradePrice();


    /**
     * Tests to see if product has traded during current session.
     *
     * @return true if product has traded
     */
    boolean hasTradedInSession();

	/**
	 * Tests to see if the product is in a state that allows trading.  Trading can take place in
     * either OPEN or FAST_MARKET states.
	 *
	 * @return true if product is in a trading state
	 */
	boolean inTradingState();

	/**
	 * Tests to see if the product has been enabled for the current session.
	 *
	 * @return true if product is enabled
	 */
	boolean isEnabledForSession();

	/**
	 * Tests to see if product is a strategy.
	 *
	 * @return true if product is a strategy
	 */
	boolean isStrategy();

    /**
     * Determines if price is valid for an order.
     *
     * @param premiumPrice premium price to be checked
     * @return true if price is valid
     */
    boolean isValidPriceForOrder(Price premiumPrice);

    /**
     * Determines if price is valid for a quote.
     *
     * @param premiumPrice premium price to be checked
     * @return true if price is valid
     */
    boolean isValidPriceForQuote(Price premiumPrice);

	/**
	 * Checks bid/ask value to see if spread is within exchange prescribed
	 * width.
	 *
	 * @param bidPrice value of bid
	 * @param askPrice valid of ask
	 * @return true if spread is within exchange prescribed width.
	 */
	boolean isWithinPrescribedWidth(Price bidPrice, Price askPrice);

    /**
     * Checks the bid/ask value to see if the spread is within the exchange prescribed width.
     * This will use the Oepw or LeapsOepw depending on wether the Product is a LEAP or not
     *
     * @param bidPrice value of bid
     * @param askPrice value of ask
     * @return true if spread is within Oepw or LeapsOepw
     */
    boolean isWithinOpeningPrescribedWidth(Price bidPrice, Price askPrice);


	/**
	 * Returns quote received status for this product.
	 *
	 * @return true if quotes have been received
	 */
	boolean quoteReceived();

	/**
	 * Remove product listener
	 *
	 * @param listener the listener to be removed
	 */
	void removeProductListener(TradingProductListener listener);

	/**
	 * Sets status of product in current session.
	 *
	 * @param status new status of product in session
	 */
	void setEnabledForSession(boolean status);

    /**
     * Sets trading status of product in current session.
     *
     * @param status new trading status
     */
    void setHasTradedInSession(boolean status);

        /**
         * Return true if all legs are valid
         */
        boolean areAllLegsValid();

    /**
     * Request to process that the Quote Locked
     */

    void quoteLocked();

     /**
      * Informs that the quote is not longer locked
      */
    void quoteNoLongerLocked();

    long getLastQuoteLockedTime();

    /**
     * Product level to check if the QuoteLock logic needs to be applied.
     * which should consider the product state to determine.
     */
    boolean isQuoteLockMinimumTradeQuantityNeeded();
    boolean isQuoteLockProcessingNeeded();
    boolean isQuoteLockNotificationNeeded();
    boolean isQuoteLockNotificationImmediate();

    /**
     * Returns true is the product is a LEAP.  LEAP determined as expiration date greater than 9 months
     */
    boolean isLeap();

    /**
     * Product level to check if its intial preOpen EOP command has already started or not,
     * since it should be only started once
     * @return true if it has been already started
     */
    boolean isAlreadyStartedPreOpenEOP();
    /**
     * set the flag to indicate that its intial preOpen EOP command has already started or not
     * @param alreadyStartedPreOpenEOP
     */
    void setAlreadyStartedPreOpenEOP(boolean alreadyStartedPreOpenEOP);

    /**
     * Returns Closing Ask and Bid Sizes for EOD Procesing in TPF.
     * @return array of int for ask and bid volumes.
     */
    int[] getClosingSizes();
    
    /**
     * return if a trading product is a buy write
     * @return
     */
    public boolean isBuyWrite();
    
    boolean isTradableCrossProduct();
    

    /**
     * Returns Closing Ask and Bid Prices for EOD Procesing in TPF.
     * @return array of int for ask and bid volumes.
     */
    Price[] getClosingPrices();

    void addAuctionToProduct(AuctionInternal auctionToAdd, boolean isActive);

    void setAuctionInHALTrigger(AuctionInternal auction);

    AuctionInternal getAuctionInHALTrigger();

    AuctionInternal getActiveAuction();

    AuctionInternal[] getInactiveAuctions();

    AuctionInternal getAuctionById(long auctionKey);
    
    java.util.ArrayList getStrategyProducts ();
    
    public boolean isOrderBookCreationComplete();
   
    public void setOrderBookCreationComplete(boolean p_isOrderBookCreationComplete);

    /**
     * Accept a command for execution within a product-level lock.  If no product level lock
     * exists for this TradingProduct, then the call is delegated to the class-level lock.
     */
    void acceptCommand(TradingClassCommand p_command, boolean p_ignoreQueueLimit) throws NotAcceptedException;

    /**
     * @return true if there is a product-level lock available for this product.
     */
    boolean hasProductLock();
    
    void setHasProductLock(boolean p_b);
    
    String getProductLockId();
    
    public long getLastTradeTradeId();
    
    public void setLastTradeTradeId(long p_lastTradeTradeId);
    
    public int getQuoteSequenceNumberForFailOver();
    
    public Price getExchangePrescribedWidthForStrategy(Price basePrice);
    
    public int getPriceProtectionForStrategy();
    
    public void calculatePriceProtectionForStrategy();
    
    /**
     * 2/2009 - rule == two largest legs cannot have a greater than 3:1 ratio
     * @return
     */
    public boolean isTradableRatioForOptionStrategy();
    /**
     * 2/2009 - rule == 2 legged buy-writes cannot have a greater than 100:8 ratio
     * @return
     */
    public boolean isTradableRatioForSimpleBuyWrites();
     
    
    public MarketData getMarketData(String sessionName);
    
    //public Price getBOTRPrice(String sessionName, Side side);
    public NBBOStruct getBOTR(String sessionName);
    
    public NBBOStruct getNBBO(String sessionName);
    
    public Price getMKTOrderRestrictedPrice(Price basePrice, boolean sell);
    
    public Quote getpendingCancelQuoteForUser(String userId);
        
    public boolean addQuoteToPendingCancelQuoteList(Quote p_acceptCancelQuoteList);
    
    public void cancelPendingQuotesForProductIfAny(OrderBook orderBook);
    
    public void processPendingCancelQuotesForProduct();
    
    public void notifyLastSaleListeners(Price lastSalePrice);
    
    public boolean isPendingCancelQuoteInProcess();

    public void setPendingCancelQuoteInProcess(boolean p_pendingCancelQuoteInProcess);
    
    public void resetDerivedQuote() throws DataValidationException, SystemException;
    
    boolean isCurrentMarketChangeCommandQueued();
    
    boolean setCurrentMarketChangeCommandQueueFlag (boolean on);
    
    TradingClassCommand getStrategyBookProcessingCommand();
    
    public boolean isProxy();
    
    public void setClosingAsk(Price newValue);
    public void setClosingBid(Price newValue);
    public void setClosingBidSize(int newSize);
    public void setClosingAskSize(int newSize);
    public void setCurrentStateCode(short newCode);
    public void setTradingClass(TradingClass tradingClass);
    public int getReportingClassKey();
    
    public void setInternalFailoverState(short p_internalFailoverState);
    public short getInternalFailoverState();
    
    /**
     * @param capDetailsMap
     * set the cap details for firms for PMM allocation.
     */
    public void setPMMCapDetails(HashMap<Integer,HashMap<String,Integer>> capDetailsMap);
    
    /**
     * @return
     * get the cap details for firms for PMM allocation.
     */
    public HashMap<Integer,HashMap<String,Integer>> getPMMCapDetails();
    
    public boolean isRolledoutSpreadClass() throws DataValidationException;
    
    public OrderBookSummaryStruct[] getLegBestMarkets();
    
    public void setLegBestMarkets(OrderBookSummaryStruct[] legOrderBooks);
    
    boolean getShortSellTriggerMode();
    
    void setShortSellTriggerMode(boolean shortSellTriggerMode);

}
