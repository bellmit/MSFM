package com.cboe.interfaces.domain;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.OrderBookTradableNotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.BookDepthStructV2;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.marketData.BookDepthDetailStruct;
import com.cboe.idl.marketData.CurrentMarketStateChangeStruct;
import com.cboe.idl.util.RoutingParameterStruct;

/**
 * Server side interface for the Order Book.
 * One Order Book exists for each product.
 * Internal calls for a particular product is made
 * directly to this interface, not to the
 * Order Book Service.  Inherits from the Order Book
 * interface as defined in the Domain Objects package.
 *
 * @version 0.50
 * @author Kevin Park
 */

public interface OrderBook {

/**
 * Updates cancel quantity in Order and updates the OrderBook
 * structure if required.  BestBook may change due to this action.
 *
 * @param aTradable Tradable
 * @param quantity int
 * @exception com.cboe.exceptions.OrderBookTradableNotFoundException
 */
void acceptCancel(Tradable aTradable, int quantity) throws OrderBookTradableNotFoundException;

String getAuctionedOrderIdString();
void setAuctionedOrderIdString(String AuctionedOrderIdString);
/**
 * Add an order to the OrderBook structure.
 * Notify BBBO and OrderAcceptedByBook Consumers.
 *
 * @param aTradable Tradable
 */
void acceptTradable(Tradable aTradable) throws DataValidationException;
/**
 * Returns true if Tradable betters the same side but does not cross
 * the opposite side.
 *
 * @return boolean	true if betters market
 * @param Tradable	new tradable
 */
boolean bettersMarket(Tradable aTradable);
/**
 * Returns the market balance price if the book is crossed.  Otherwise,
 * returns an indicator to signal market imbalance and direction or an
 * indication that the book is not crossed.
 *
 * @return com.cboe.businessServices.orderBookService.OpeningPriceIndication
 */
OpeningPriceIndication calculateExpectedOpeningPrice() throws DataValidationException;
/**
 * Returns true if price of tradable matches or crosses the best book
 * on the opposite side.  If the new tradable has MKT price, return true
 * if the book quote is within the exchange prescribed width.
 * MKT tradables on the same side as the new tradable may be ignored.
 *
 * @return boolean	cross indicator
 * @param Tradable	new tradable
 * @param Price		exchange prescribed width
 */
boolean crossesMarket(Tradable aTradable, Price exchangePrescribedWidth);
/**
 * Returns true if price of tradable matches or crosses the best book
 * on the opposite side.  If the new tradable has MKT price, return true
 * if the book quote is within the exchange prescribed width.
 * MKT tradables on the same side as the new tradable may be ignored.
 *
 * @return boolean	cross indicator
 * @param Tradable	new tradable
 * @param Price		exchange prescribed width
 */
boolean crossesMarket(Side tradableSide, Price tradablePrice, Price exchangePrescribedWidth);
/**
 * Return best book information for this product.
 * If no orders exist on one side, default price (no_price type) and volume
 * of zero returned.
 *
 * @return	BestBook
 */
public BestBook getBestBook();
/**
 * Returns a structure that represents a summary of all orders
 * in the book for this product.  The orders are categorized
 * into sides and prices.
 *
 * @param includeTopOnly if true, only the top (N) prices of each side will be included
 * @return	BookDepthStruct
 */
public BookDepthStruct getBookDepth(boolean includeTopOnly);
/**
 * Returns a structure that represents a summary of all orders
 * in the book for this product.  The orders are categorized
 * into sides and prices. Special method for hybrid project
 * @ Mike Hasbrouck 03/2003
 * @param includeTopOnly if true, only the top (N) prices of each side will be included
 * @return	BookDepthStructV2
 */
public BookDepthStructV2 getBookDepthV2(boolean includeTopOnly);
/**
 * Returns a structure that represents a summary of all orders
 * in the book for this product.  The orders are categorized
 * into sides and prices with order type detail include.
 *
 * @return	BookDepthDetailStruct
 */
public BookDepthDetailStruct getBookDepthDetail();
/**
 * Returns a price that represents the value calculated
 * by subtracting the best buy price in the book from the
 * best sell price.  Market orders are ignored and the
 * returned price maybe negative.
 *
 * @return bookWidth	if NoPrice, either the best buy,
 *						the best sell or both are missing.
 */
Price getBookWidth();

/**
 * Returns the product key for this order book.
 *
 * @return int		product key
 */
int getProductKey();
/**
 * Returns the total volume for all tradables in the book at the specified
 * side that is at or better than the specified price.  All volume for the
 * contingent orders at the price, as well as market order volume is
 * included.  If the specified price is market, only the market volume is
 * returned.
 *
 * @return int		total quantity ahead of a price
 * @param Side		book side in question
 * @param Price		the worst price level to be considered
 */
int getQuantityAhead(Side aSide, Price aPrice);

public int getNonOddLotQuantityAhead(Side aSide, Price aPrice);

public boolean hasProtectedOrders(Side aSide, Price aPrice);

public int getLargestProtectedOrderQty(Side aSide, Price aPrice);

/**
* Returns the total volume for all tradables in the book at the specified
* side that is at or better than the specified price.  All volume for the
* contingent orders at the price, as well as market order volume is
* included.  If the specified price is market, only the market volume is
* returned.
* This method restricts quantiy ahead to customer quantity (if manual quote exists)
* and to (customer size + class.autoExSize) of MQ does not exists
* 
*
* @return int       total quantity ahead of a price
* @param  boolean   true if manualQuoteExists
* @param Side       book side in question
* @param Price      the worst price level to be considered
*/

IndexHybridAutoExQtyHolder getQuantityAheadIndexHybrid(Side aSide, Price aPrice,int aSize, boolean manualQuoteExists);

/**
 * Returns the total volume for all tradables in the book at the specified
 * side that is at or better than the specified price considering the 3 volumes,  
 * volume for the order quantity, min volumn contingency quantity and max volumn contingency quantity
 * Please note that the volumns returned might contain volume contingency orders in the book so it the trade
 * might not be sucessful
 * Added for Cross Product Leg trading.
 * @param aSide
 * @param incomingTradablePrice
 * @param originalQuanity
 * @param minVolumeContingencyQuantity
 * @param maxVolumeContignencyQuantity
 * @return
 */
public int getQuantityAhead(Side aSide, Price incomingTradablePrice, int nonContingencySize, int minVolumeContingencyQuantity, int maxVolumeContignencyQuantity);


/**
 * Returns tradables in this order book for the given
 * side in priority sequence.
 *
 * @return Enumeration
 * @param aSide com.cboe.domain.util.Side
 */
Enumeration getTradables(Side aSide);
/**
 * Returns tradables in this order book for the given
 * side.  Just enough tradables are returned to satisfy
 * the given quantity in priority sequence.  If there
 * are not enough tradables in this order book, tradables
 * returned may not satisfy the required quantity.
 *
 * @return Enumeration
 * @param aSide com.cboe.domain.util.Side
 * @param aQuantity int
 */
Enumeration getTradables(Side aSide, int aQuantity);

/**
 * This method is used for book comparison between Master and Slave side only.
 * @param aSide
 * @return
 */
public Enumeration getTradablesForBookCompare(Side aSide);

/**
 * Returns tradables in this order book for the given
 * side.  All tradables that may trade at the given price
 * is returned in priority sequence.  If the given price
 * is market, only market orders will be returned.
 *
 * @return Enumeration
 * @param aSide com.cboe.domain.util.Side
 * @param aPrice Price
 */
Enumeration getTradables(Side aSide, Price aPrice);
/**
 * Returns tradables in this order book for the given
 * side.  All tradables that may trade at the given price
 * is returned in priority sequence.  If the given price
 * is market, only market orders will be returned.
 * If includeMarkets is false, market orders will be skipped.
 *
 * @return Enumeration
 * @param aSide 			side in question
 * @param aPrice 			constraint price
 * @param includeMarkets	return market price
 */
Enumeration getTradables(Side aSide, Price aPrice, boolean includeMarkets);
/**
 * Returns tradables in this order book at the give side and price
 *
 * @return Enumeration
 * @param aSide the side on which to retireve tradables
 * @param aPrice  the price at which to retireve tradables
 */
Enumeration getTradablesAtPrice(Side aSide, Price aPrice);
/**
 * Returns the trading product of this order book.
 *
 * @return TradingProduct
 */
TradingProduct getTradingProduct();
public boolean isSideItemPresent(Side aSide);
/**
 * Returns true if there are MKT tradables on the specified side.
 *
 * @return	boolean	true if market tradable found on this side
 * @param	Side	the side to check for MKT tradables
 */
boolean hasMarketTradables(Side aSide);

/**
 * Returns true if Tradable matches the same side market.
 *
 * @return boolean	true if matches market
 * @param Tradable	new tradable
 */
boolean matchesMarket(Tradable aTradable);
/**
 * Returns true if Tradable is at a worse price than the same side
 * market.
 *
 * @return boolean	true if off market
 * @param Tradable	new tradable
 */
boolean offMarket(Tradable aTradable);
/**
 * Tells order book that a transaction has completed and if the best book
 * was updated, it should be re-calculated.
 *
 */
void refreshBestBook();

/**
 * If the refesh is on a fast failover, then we want to make sure
 * the order book is marked as updated so that the current market
 * will be published.
 * @param p_markBookAsUpdated
 */
void refreshBestBook(boolean p_markBookAsUpdated);

/**
 * Reinstate quantity for a tradable at the original time priority.
 * The tradable may or maynot still exist in the book.
 *
 * @param     aTradable Tradable
 * @param     reinstateQuantity int
 */
void reinstateQuantity(Tradable aTradable, int reinstateQuantity) throws DataValidationException;
/**
 * Unbooks the whole remaining quantity of the tradable from order book.
 *
 * @param	aTradable Tradable
 */
void unbook(Tradable aTradable, int unbookQuantity, Side aSide, Price aPrice) throws OrderBookTradableNotFoundException;
/**
 * Updates remaining tradable quantity by the amount specified.
 *
 * @param	aTradable Tradable
 * @param	reduceQuantity int
 */
void update(Tradable aTradable, int qtyDelta, boolean updateAppliedToTradable) throws OrderBookTradableNotFoundException;

boolean isEmpty();

/**
 * Update OrderBook structure due to orders that were fully
 * or partially traded.  BestBook may be updated due to this action.
 * Second parameter passed is enumeration of
 * com.cboe.businessServices.brokerService.ParticipantItem.
 * The interface is a comprimise between saftey and performance.  Saftey, since order book will be able to catch problems of
 * trying to unbook tradables that are not in the book.  Performance, since Broker does not have to copy the list of participant
 * items and remove the one that is not needed.  It is ugly but it works.
 *
 * @param side com.cboe.domainObjects.util.Side
 * @param participantItems Enumeration
 * @param unBookedTradable Tradable, this tradable is in the list of participant items but not a booked tradable.  OrderBook should
 *						ignore this tradable when unbooking tradables.  If this is null, there is nothing to ignore.
 *
 * @exception com.cboe.businessServices.orderBookService.OrderBookTradableNotFoundException
 */
void updateStructure(Side side, java.util.Enumeration participantItems, Tradable unBookedTradable) throws OrderBookTradableNotFoundException;
/**
 * Returns true if the best book width is equal to or
 * narrower than the width specified.
 *
 * @return	boolean	true if best book width within given width
 * @param	Price	width to compare against
 */
public boolean widthNarrowerOrEqual(Price widthToCheck);

/**
 * Gets standard quotes in the order book.
 */
Quote[] getStandardQuotes();

/**
 * gets best non contingent bid price
 */
Price getBestNonContingentBidPrice();

/**
 * gets best non contingent ask price
 */
Price getBestNonContingentAskPrice();

    /**
     * query if the current market is the NBBO on the given side
     */
    public boolean isCurrentMarketNBBO(Side aSide, NBBOStruct nbbo );

    /**
     * query if the current market is equal to or better than a price that is an "amount"
     * better/worse than the NBBO.
     *
     * @param aSide the side of the book to check
     * @param amount an amount to add(subtract) to the NBB(NBO)
     * @param better indicates if amount should better (true) or worsen (false) the NBBO prior to comparison
     */
    public boolean isCurrentMarketNBBO(Side aSide, Price amount, boolean better, NBBOStruct nbbo);

    /**
     * query if the current market is equal to or better than a price that is an "amount"
     * better/worse than the NBBO.
     *
     * @param aSide the side of the book to check
     * @param amount an amount to add(subtract) to the NBB(NBO)
     * @param better indicates if amount should better (true) or worsen (false) the NBBO prior to comparison
     * @param nonContingentOnly indicates that we should look for NBBO amongst none contingent orders only, otherwise we should look for NBBO out of all limit orders on the given side of the book
     */
    public boolean isCurrentMarketNBBO(Side aSide, Price amount, boolean better, NBBOStruct nbbo, boolean nonContingentOnly);
    
    public boolean isCurrentMarketNBBOForOddLots (Side aSide, NBBOStruct nbbo );

    /**
     * Returns the total volume for all NonQ tradables in the book at the specified
     * side that is at or better than the specified price.  All volume for the
     * contingent orders at the price, as well as market order volume is
     * included.  If the specified price is market, only the market volume is
     * returned.
     *
     * @return int		total nonQ quantity ahead of a price
     * @param Side		book side in question
     * @param Price		the worst price level to be considered
     */
    public int getNonQQuantityAhead(Side aSide, Price aPrice);

    /**
     * Determine if a tradable is a nonQ tradable
     */
    public boolean isNonQTradable(Tradable aTradable);

    /**
     * If the current best book is locked
     */
    public boolean isLocked();

    /**
     * Returns the list of tradables locking the book on one side
     */
    public Enumeration getLockedTradables(Side side);

    /**
     * Returns the list of widened tradables when book is locked on one side
     */
    public Enumeration getWidenedTradables(Side side);

    /**
     * Returns the best Q-tradable price for one side
     */
    public Price getBestQPrice(Side side);

    /**
     * Returns the best Q-tradable quantity for one side
     */
    public int getBestQQuantity(Side side);
    
    /**
     * Returns the best Quote price for one side
     */
    public Price getBestQuotePrice(Side side);

    /**
     * Returns the best Quote quantity for one side
     */
    public int getBestQuoteQuantity(Side side);

   /**
    * the blow two methods are similar to the logic of refreshBestBook,
    * but we devide it into two methods and exported to outside, because
    * we need to add the quote locking check between calling these two methods in brokerProcessor
    * since that new step might change the book again, which also might change the lock status again
    * so when quote locking is On, these two method should be called separately instead of calling refreshBestBook()
    * preProcessRefreshBestBook()
    * refreshBestBookWithLock()
    * they only need to be called in those steps where need to re-check quote lock status.
    */
    public void preProcessRefreshBestBook();
    public void refreshBestBookWithLock();

    /**
     * Return boolean to indicate if an active QuoteTrigger exists on specified side of the book.
     * Note: An active QuoteTrigger exists, but it may not be booked.
     *
     */
    public boolean hasQuoteTrigger(Side aSide);

    /**
     * Return boolean to indicate if an active QuoteTrigger is booked.
     *
     */
    public boolean hasBookedQuoteTrigger();

    /**
     * Return the active quote trigger on the specified side of the book. If there is none,
     * return null
     */
    public QuoteTriggerTradable getQuoteTrigger(Side aSide);

    /**
     * create a new QuoteTrigger for the specified side and by type
     */
    public QuoteTriggerTradable createQuoteTrigger(Side aSide, short triggerType);

    /**
     * cleanup after quote trigger expires
     */
    public void quoteTriggerExpired(Side aSide);

    /**
     * return best nonQ price on one side
     */
    public Price getBestNonQPrice(Side aSide);

    public int getBestNonQQuantity(Side aSide);

    public int getNonQNonContingentQuantityForPrice(Side aSide, Price atPrice);

    public int getQQuantityForPrice(Side aSide, Price atPrice);

    /**
     * return best price on one side
     */
    public Price getBestPrice(Side aSide);

    /**
     * return best limit price on one side
     */
    public Price getBestLimitPrice(Side aSide);

    /* Get all the orders from the book corresponding to the product key.
     *
     * @param sessionName The session name where the method has been invoked.
     * @param productKey Information on the product that is being moved
     *
     * @return boolean All the orders in a struct.
     *
     * @throw SystemException
     * @throw CommunicationException
     * @throw AuthorizationException
     * @throw DataValidationException
     * @throw TransactionFailedException
     * @throw NotAccetedException
     * @author Sandip Chatterjee
     */
public BookDepthDetailedStruct getDetailedOrderBook(String sessionName,int productKey)
    throws SystemException,
           CommunicationException,
           AuthorizationException,
           DataValidationException,
           NotFoundException,
           NotAcceptedException;

/**
 * Set method for the transient target state of the order book
 * @author Sandip Chatterjee
 */
public void setTargetState(boolean targetState);

/**
 * Get method for the transient target state of the order book
 * @author Sandip Chatterjee
 */
public boolean getTargetState();

/**
 * Set method for the transient current state of the order book
 * @author Sandip Chatterjee
 */
public void setCurrentState(boolean currentState);

/**
 * Get method for the transient current state of the order book
 * @author Sandip Chatterjee
 */
public boolean getCurrentState();

/**
*   To cancel all the OPG Orders.
*   @author Sandip Chatterjee
*/
public void cancelAllOPGOrders() throws DataValidationException;

/*   To cancel all the Marketable Quotes that are in the book after the Opening..
*   @author Sandip Chatterjee
*/
public void cancelAllMarketableQuotes() throws DataValidationException;

/*
* To get the quantity for the opening trade when doing the open manually
*/
public int getOpeningTradeQuantity(Price aPrice);

/**
 * This method is responsible to allocate the total quantity (aVolumeConstraint)
 * to tradables in the book on the side (aSide), and upto the price (aPrice), and return
 * an iterator of priority sets from highest priority to lowest priority.  If a priority
     * set of market priced tradables are returned, the trade price is set to a limit priced as
     * determined by the allocation strategy.
     *
     * @return Enumeration	of PriorityTradbleSet that meets the criteria
     * @param Side: book side in question
     * @param Price: limit price
     * @param int: desired trade quantity
     * @param AllocationTradeContext: trade context for this allocation
     */
     public Iterator getPrioritySets(Side aSide, Price aPrice, int aVolumeConstraint, AllocationTradeContext aTradeContext);

    /**
     * This method is responsible to allocate the total quantity appropriately to
     * the tradables passed in, based on the configured allocation strategies for
     * this book and allocation trade context. Returns an iterator of PriorityTradableSet
     * in priority order same as the order of tradables passed in.
     *
     * @return Iterator of PriorityTradbleSet
     * @param ArrayList priceDetails: a list of PriceDetails of tradables
     * @param Price aPrice: the price on price the trade will take place
     * @param int volumeConstraint: total availabe quantity to be allocated to those tradables.
     * @param AllocationTradeContext: trade context for this allocation
     */
     public Iterator getPrioritySets(ArrayList aPriceDetails, Price aPrice, int aVolumeConstraint, AllocationTradeContext aTradeContext);

    /**
     * Inquires if OrderBook is within the legal market
     * @return true if the OrderBook is within the legal market
     * @param none
     */
    public boolean isLegalMarket();

    /**
     * Gets current market product state change as struct.  Will be element of 1.
     * @return CurrentMarketStateChangeStruct[]
     * @param oldState - old product state
     * @param newState - new product state
     */
    public CurrentMarketStateChangeStruct[] toCurrentMarketStateChangeStructs(short oldState, short newState, boolean flagRepublish);

    public RoutingParameterStruct getRoutingParameter();

    boolean improvesThisSideMarket(Price tradablePrice, Side tradableSide);
    
    /**
     * Accept the manual quote and puts it in the manual book.
     * @param manualQuote
     */
    public void acceptManualQuote(ManualQuote manualQuote);
    
    /**
     * cancel the manual quote from the manual book.
     * @param manualQuote
     * @throws OrderBookTradableNotFoundException
     */
    public void acceptCancelManualQuote(ManualQuote manualQuote)throws OrderBookTradableNotFoundException;
    
    /**
     * cancel the manual quote from the manual book.
     * @param sideToCancel
     * @throws OrderBookTradableNotFoundException
     */
    public void acceptCancelManualQuote(Side sideToCancel);
    
    /**
     * cancel the manual quote from the manual book.
     * @param sideToCancel
     * @throws OrderBookTradableNotFoundException
     */
    public void acceptCancelManualQuote(Side sideToCancel, char reason);
    
    /**
     * display manual book 
     */
    public String displayManualBook();
    
    /**
     *empty the manual book 
     */
    public void clearManualBook();
    
    public void clearConsolidatedManualBook();
    
    /**
     * This methods checks whether tradable is marketable with the opposite side manual quote.
     * If opposite side manual quote does not exists, It will return false.
     *  @param tradablePrice - price of incoming tradable for which to compare the price
     *  @param tradableSide - side of incoming tradable for which to compare the price
     */
    public boolean crossesManualMarket(Price tradablePrice, Side tradableSide);
    
    /**
     * Check If incoming tradable is at a price worse than manual quote on the same side and return false;
     *  @param tradablePrice - price of incoming tradable for which to compare the price
     *  @param tradableSide - side of incoming tradable for which to compare the price
     */
    public boolean offManualMarket(Price tradablePrice, Side tradableSide);
    
    /**
     * Check wheather given tradable improves the manual book on the same side.
     *  @param tradablePrice - price of incoming tradable for which to compare the price
     *  @param tradableSide - side of incoming tradable for which to compare the price
     * @return true if tradable improves the manual book. 
     * If manual quote does not exists, it will return true.
     */
    public boolean improvesManualMarket(Price tradablePrice, Side tradableSide);
    
    public ManualQuoteBook getManualQuoteBook();
    
    public ConsolidatedManualBook getConsolidatedManualBook();

   /**
     *  Return the best of both(book and manual book) non contingent price.
     * @param side Side for which you want to get the price
     * @return
     */
    public Price getBestOfBothNonContingentPrice(Side side);
    public int getBestCustomerOrderQuantity(Side aSide);
    
    /**
     * Is the best quote like tradables (quote or I order) within EPW
     * @return
     */
    public boolean isBestQPricesWithinEPW();
    
    /**
     * Is the best quote like tradables (quote or I order) within OEPW
     * @return
     */
    public boolean isBestQPricesWithinOEPW();
    
    /**
     *  Return the best of both(book and manual book) non contingent Qty.
     * @param side Side for which you want to get the qty
     * @return
     */
    public int getBestOfBothNonContingentQuantity(Side side);
    
    /**
     * Return the maximum quantity of any single order at the best price
     * of the specified side.
     * @param aSide
     * @return The maximum quantity of any single order.
     */
    public int getBestNonQuoteLikeMaxSingleOrderQuantity(Side aSide);
    
    /**
     *  Return the best of both(book and manual book) price.
     * @param side Side for which you want to get the price
     * @return
     */
    public Price getBestOfBothPrice(Side side);
    
    public void setActiveAuction(boolean flag);

    /**
     * Returns true if Tradable is at a worse price than the same side CBOE
     * current market.
     */
    boolean offCurrentMarket(Tradable aTradable);

    /**
     * Returns true if Tradable matches the same side CBOE current market
     */
    boolean matchesCurrentMarket(Tradable aTradable);
    
    boolean matchesCurrentMarket(Side aSide, Price aPrice);
    
    boolean needsDpmQuoteToOpen();
    
    boolean needsMMQuoteToOpen();
    
    boolean getEnabledOpenImmediateConfigured();
    
    public void refreshBookDepth();    
 
    IndexHybridAutoExQtyHolder getIndexHybridAutoExQtyHolder();    
    void resetIndexHybridAutoExQtyHolder();   
    
    enum BestBookUpdatedSide
    {
        NEITHER, BID, ASK, BOTH;
        
        public final BestBookUpdatedSide plus(Side side)
        {
            switch (this)
            {
                case NEITHER:
                    return (side.isBuySide() || side.isAsDefinedSide()) ? BID : ASK;
                case BID:
                    return (side.isBuySide() || side.isAsDefinedSide()) ? BID : BOTH;
                 case ASK:
                    return (side.isBuySide() || side.isAsDefinedSide()) ? BOTH : ASK;
                 case BOTH:
                    return this;
            }
            return NEITHER;
        }
    };    
    public Map<BestBookUpdatedSide, BestPriceStruct> getBestPriceStruct();
    
    public void evalMarketStatus();
    
    public void rollbackTX() throws Exception;
    
    
    /**
     * If the accessToTheOrderbook with in the class level lock (which is the valid and right thing to do)
     * then, this method returns true;
     * 
     * If not, returns false and care has to be taken. 
     * @return boolean
     */
    public boolean isAccessWithinClassLevelLock();
    
    public void bookTradableWithoutPublish(Tradable aTradable) throws DataValidationException;
    
    public enum BookUpdateAction
    {
    	ALL, ALL_AS_APPLICABLE, RESET; 
    }
    public void rollbackTransactionAndRebuildTheBook() throws Exception;

    public int getBestNonQQuantityForSpread(Side side);
}
