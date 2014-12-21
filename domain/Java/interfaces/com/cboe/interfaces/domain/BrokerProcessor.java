package com.cboe.interfaces.domain;

import java.util.List;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiQuote.QuoteStructV4;
import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.OrderHandlingStruct;
import com.cboe.interfaces.domain.optionsLinkage.AllExchangesBBO;
import com.cboe.interfaces.domain.optionsLinkage.SweepElement;


/**
 * A processor of accepted <code>Broker</code> requests.  Requests are put through a queue
 * and an instance of this class is called to complete the processing of the request.
 *
 * @author John Wickberg
 * @since increment 5
 */

public interface BrokerProcessor {

    /**
     * Checks any orders that are being held to see if they can now trade.
     *
     * @param book order book to be checked
     * @param rfqResponse true if check being done after RFQ timeout
     * @return true if book still contains market orders
     * @exception DataValidationException if created trades fail validation
     * @exception SystemException if we fail to create a trade report
     */
    boolean checkBookedOrders(OrderBook book, boolean rfqResponse) throws DataValidationException, SystemException;

    /**
     * Process ending hold
     */
    void processEndingHold(Integer productKey);
    /**
     * processes a cancel for an order.
     *
     * @param anOrder                       order to be cancelled
     * @param quantity                      quantity to cancel.
     * @return 
     * @exception DataValidationException   if the product in the <code>Order</code> cannot be found.
     * @exception SystemException           if processing of cancel fails
     */
    boolean processCancel(Order anOrder, int quantity, 
            String userAssignedCancelId, 
            boolean p_publishReportsUnconditionally) throws DataValidationException, SystemException;

    /**
     * processes a cancel response for an order.
     *
     * @param anOrder                       order to be cancelled
     * @param quantity                      quantity to cancel.
     * @param userAssignedCancelId          user assigned cancel request id
     * @param cancelRequestId                     cboe assigned cancel request id
     * @exception DataValidationException   if the product in the <code>Order</code> cannot be found.
     * @exception SystemException           if processing of cancel fails
     */
    void processCancelResponse(Order anOrder, int quantity, String userAssignedCancelId, long cancelRequestId) throws DataValidationException, SystemException;

    /**
     * processes a Linkage order cancel due to timeout.
     *
     * @param anOrder                       order to be processed
     * @exception DataValidationException   if the product in the <code>Order</code> cannot be found.
     * @exception SystemException           if processing of cancel fails
     */
    int processLinkageOrderTimeout(Order anOrder) throws DataValidationException, SystemException;

    /**
     * Processes a <code>Quote</code> cancel.
     *
     * @param currentQuote                      quote to be cancelled
     * @exception DataValidationException   if the <code>Quote</code> is for an unknown product
     * @exception SystemException           if processing of cancel fails
     */
    void processCancel(Quote currentQuote) throws DataValidationException, SystemException ;

    /**
     * Processes a cancel replace response for an order.
     *
     * @param originalOrder                 order to be cancelled
     * @param quantity                      quantity to cancel
     * @param newOrder                      order to replace the original order with
     * @param userAssignedCancelId          user assigned cancel request id
     * @param cancelRequestId                     cboe assigned cancel request id
     * @param processNewOrder               should the new order be processed
     * @exception DataValidationException   if the product for the <code>Order</code> cannot be found.
     */
    void processCancelReplaceResponse(Order originalOrder, int quantity, Order newOrder, String userAssignedCancelId, long cancelRequestId, boolean processNewOrder) throws DataValidationException, SystemException;

    /**
     * Processes a cancel replace for an order.
     *
     * @param originalOrder                 order to be cancelled
     * @param quantity                      quantity to cancel
     * @param newOrder                      order to replace the original order with
     * @exception DataValidationException   if the product for the <code>Order</code> cannot be found.
     */
    void processCancelReplace(Order originalOrder, int quantity, Order newOrder, String userAssignedCancelId) throws DataValidationException, SystemException;
    /**
     * Process a <code>Cross</code>
     *
     * @param aCross    cross to be executed.
     * @exception DataValidationException if the <code>cross</code> is for an unknown product
     * @exception SystemException if we fail to create a trade report
     */
    void processCross(Cross aCross) throws DataValidationException, SystemException;
    
    
    /**
     * Process Qualified contingent trade orders.
     * @param aCross
     * @throws DataValidationException
     * @throws SystemException
     */
    void processQCTOrder(Cross aCross)throws DataValidationException, SystemException;
    
    /**
     * Process an <code>Order</code>
     *
     * @param anOrder                       order to be traded.
     * @exception DataValidationException   if the <code>Order</code> is for an unknown product
     * @exception SystemException if we fail to create a trade report
     */
    void processOrder(Order anOrder) throws DataValidationException, SystemException;
    
    /**
     * Process an <code>Order</code>
     *
     * @param anOrder                       order to be traded.
     * @exception DataValidationException   if the <code>Order</code> is for an unknown product
     * @exception SystemException if we fail to create a trade report
     */
    void  processRerouteCrossOrder(Order anOrder) throws DataValidationException, SystemException;

    /**
     * Process an <code>Order</code> with min size quarantee at the price specified
     *
     * @param anOrder                       order to be traded.
     * @exception DataValidationException   if the <code>Order</code> is for an unknown product
     * @exception SystemException if we fail to create a trade report
     */
    void doMinSizeOrderProcessing(Order anOrder, OrderBook book, int minSize, Price minSizePrice) throws DataValidationException, SystemException;

    /**
     * Process an <code>Order</code> using normal processing strategy
     *
     * @param anOrder                       order to be traded.
     * @param book                          the order book corresponding to the order
     * @exception DataValidationException   if the <code>Order</code> is for an unknown product
     * @exception SystemException if we fail to create a trade report
     */
    void doNormalOrderProcessing(Order anOrder, OrderBook book, Price botr) throws DataValidationException, SystemException;
    /**
     * Process an <code>Quote</code>
     *
     * @param newQuote                      quote to be traded.
     * @exception DataValidationException   if the <code>Quote</code> is for an unknown product
     * @exception SystemException if we fail to create a trade report
     */
    void processQuote(Quote newQuote, int sessionKey) throws DataValidationException, SystemException;
    /**
     * Process an <code>Order</code> reinstatement
     *
     * @param order         order to be reinstated.
     * @exception DataValidationException   if the <code>Order</code> is for an unknown product
     */
    void processReinstateOrder(Order order, long tradeId, int quantity) throws DataValidationException, SystemException;
    
    /**
     * Process an <code>Order</code> AddQuanitity
     *
     * @param order         order to be reinstated.
     * @exception DataValidationException   if the <code>Order</code> is for an unknown product
     */
    void processAddQuantityToOrder(Order anOrder, int quantity) throws DataValidationException, SystemException;
    /**
     * Processes an <code>Order</code> update.
     *
     * @param currentOrder      order to be updated
     * @param updatedOrder      OrderStruct containing the updates to be applied to
     *                          thie order
     * @exception               DataValidationException if product for order not found
     */
    void processUpdate(Order currentOrder, OrderHandlingStruct updatedOrder ) throws DataValidationException, SystemException;
    /**
     * Processes a <code>Quote</code> update.
     *
     * @param currentQuote      quote to be updated
     * @param updatedQuote      QuoteStruct containing the updates to be applied to
     *                          this quote
     * @exception               DataValidationException if product for quote not found
     */
    void processUpdate(Quote currentQuote, QuoteStructV4 updatedQuote, int sessionKey )throws DataValidationException, SystemException;

    /**
     * Process the best book change event of a leg of a spread product
     */
    void processLegBestBookChange(int productKey);
    
    public void processLegBestBookChange(int productKey, Boolean marketImproved, boolean legTransitionToOpen);
    
    /**
    * Tells the tradable can now do post trade processing 
    * @param tradable the tradable to process the booking
    * @param book the order book to book in if appropriate
    * author Connie Liang
    */
    public void handleTradablePostprocess(Tradable tradable, OrderBook book) throws DataValidationException;
    
    /**
        * Tells the order handled for NBBO Protection is done. It can now 
        * do any post processing if needed for the order.
        * @param Order the order 
        */
    public void handlePostNBBOProtectionProcessing(Order order) throws DataValidationException;

    /**
     * Route Outbound linkage  order to away exchange
     */
    public void routeOrderToAwayExchange(Order anOrder)
            throws DataValidationException, SystemException, TransactionFailedException, CommunicationException, NotAcceptedException, AuthorizationException;


    /**
     * Return hybrid order
     */
    void returnOrder(Order anOrder, short reason) throws DataValidationException, NotAcceptedException;


    /**
     * Return hybrid cancel request
     */
    void returnCancelRequest(Order anOrder, int quantity, String userAssignedCancelId, short reason)
        throws DataValidationException, NotAcceptedException;

    /**
     * Return hybrid cancel replace
     */
    void returnCancelReplaceRequest(Order anOrder, int quantity, Order replacementOrder,String userAssignedCancelId, short reason)
        throws DataValidationException, NotAcceptedException;

    /**
     * set the Broker of this BrokerProcess
     */
    void setBroker(Broker aBroker);

    /**
     * Tells that its the time to process all the locked quotes in book for the product
     * @throws SystemException 
     */
    void processQuotesAtLockExpired(TradingProduct product) throws DataValidationException, SystemException;

    /**
     * Checks to see if any held market orders exist.  If they do,
     * also check to see if they can now be traded, after the trade complete,
     * need to check quote locking status is changed or not after the trade happens
     *
     *  NOTE: This method has been changed so that it will check
     *  if any held market orders or any contingent orders in book may trade.
     *
     *  Assumption:Q order cannot be  market order or contingent order.
     *
     * @param book order book to check
     * @param rfqResponse indicates if check being done at RFQ timeout
     * @return true if there are still market orders in the book
     * it is implemented in BrokerProcessorBase
     */
    boolean checkBookedOrdersWithLockCheck(OrderBook book, boolean rfqResponse)
            throws DataValidationException, SystemException;

    /**
     *  Checks if booked tradables can be traded
     *  @param book containing the tradables
     */
    boolean canBookedTradablesBeTraded(OrderBook book);


   /**
    * processRefreshBestBook - it will call book to refresh best book,
    * insert the quote locking to check nolongLocked and handling narrow quotes if any,
    * between calling these two methods in brokerProcessor
    * since checking book update might change the book again, which also might change the lock status again
    * so when quote locking is On, this method should be called instead of directly alling book.refreshBestBook()
    * 1. preLockCheck
    * 2.book.preProcessRefreshBestBook()
    * 3. afterLockCheck
    * 4.book.refreshBestBookWithLock()
    * It will be called in those steps where need to re-check quote lock status.
    */
    void processRefreshBestBook(OrderBook book) throws DataValidationException;


    /**
     * Process the QuoteTrigger expiration
     */
    void processQuoteTriggerExpire(QuoteTriggerTradable aQuoteTrigger)
        throws DataValidationException, SystemException;

    /*
     *  Process the opening of the product when CBOE is not the primary.
     *
     *  @param PriceStruct openingPrice
     *  @param boolean publish
     *
     *  @return void
     *
     *  @exception SystemException
     *  @exception DataValidationException
     *
     *  @author Sandip Chatterjee
     */
    public void processOpeningOfProduct(PriceStruct openingPrice_, Integer productKey_) throws SystemException, DataValidationException;

    /**
     * Processes the NBBO agent's order to satisfy the customer order for which Satisfaction order was created.
     * @param refSatisfactionOrder
     * @param nbboAgentOrder
     * @throws DataValidationException
     * @throws SystemException
     */
    void processCustomerOrderSatisfy(Order refSatisfactionOrder, Order nbboAgentOrder) throws DataValidationException, SystemException;

    /**
     * Fill the satisfaction order by allocating the size through equal distribution to all logged in MM with a quote in the class.
     * This could be used to cancel the satisfaction order with 0 crowd quantity.
     * @param satisfactionOrder
     * @param crowdQuantity
     * @param cancelRemaining
     */
    public void processSatisfactionOrderInCrowdFill(Order satisfactionOrder, int crowdQuantity, boolean cancelRemaining, short disposition) throws DataValidationException, SystemException;

    /**
     * Reject the Incoming linkage order at the request of NBBO agent.
     * @param anOrder
     * @param resolution
     */
    void processNewOrderReject(Order anOrder, short resolution);

    /**
     * process auction expiration
     * @param theCurrentAuction
     * @exception SystemException
     */
    public void  processAuctionExpire (Auction theCurrentAuction, short terminatingReason, Tradable terminatingTradable)
            throws DataValidationException,SystemException;

    /**
     * process internalization
     * @param theInternalizationPair
     * @exception SystemException
     */
    public void processInternalizationOrders (InternalizationPair theInternalizationPair)
            throws DataValidationException, SystemException;

    /**
     *  processUnauctionedFirmMatchOrder
     * @param firmOrder
     * @throws SystemException
     */
    public void processUnauctionedFirmMatchOrder(Order firmOrder) throws SystemException, DataValidationException;

    /**
     *
     *  processUnauctionedInternalizedOrder
     * @param internalizedOrder
     * @param reason
     * @throws SystemException
     */
    public void processUnauctionedInternalizedOrder(Order internalizedOrder, short reason)
    throws SystemException, DataValidationException;

    /**
     * Process class state change for auction
     * @param newState
     */
    public void setClassState(ClassStateStruct newState);

    /**
     * Process product state change for auction
     * @param classKey
     * @param sessionName
     * @param newState
     */
    public void setProductState(int classKey, String sessionName, ProductStateStruct newState);

    /**
     * Returns OrderRoutingReason
     * @param anOrder
     * @param book
     * @throws DataValidationException
     * @throws SystemException
     */
    public void doAuctionHALProcessing(Order anOrder, OrderBook book, HALStruct halStruct)
        throws DataValidationException, SystemException;

    /**
     * Process an <code>Order</code> using trade and ship processing
     * @param order
     * @param book
     * @param isOrderFlashed
     */
    public void doTradeAndShipOrderProcessing(Order order, OrderBook book, boolean isOrderFlashed)
        throws DataValidationException, SystemException;

    public void doTradeAndShipOrderProcessing(Order order, OrderBook book, HALStruct hal, boolean isOrderFlashed)
        throws DataValidationException, SystemException;
    
    /**
     * Process an <code>Order</code> using trade and ship processing
     * @param order
     * @param book
     * @param isOrderFlashed
     */
    public void doHALOTradeAndShipOrderProcessing(List<Order> orders, OrderBook book)
        throws DataValidationException, SystemException;
    
    /**
     * Set the auction type when an auction starts and ends.
     */
    public void setAuctionState(int productKey, short auctionType);

    public void tradeWithNonQOnly(Order anOrder, OrderBook book, Price expectedTradePrice, boolean isTreatedLikeCustomerOnly)
        throws DataValidationException, SystemException;

    public void processTradable(Tradable incomingTradable, OrderBook book, Price tradeEndingPrice)
        throws DataValidationException, SystemException;

    /**
     * Get the auction type for productKey
     */
    public short getAuctionState(int productKey);
    
    /**
     * process manual quote.
     */
    public void processManualQuote(ManualQuote manualQuote) throws DataValidationException, SystemException;
    
    /**
     * cancel all HAL, quote lock, quote trigger for product.
     */
    public void cancelQuoteLocknTriggernHAL (Integer productKey, Tradable tradable) throws DataValidationException, SystemException; 
    
    /**
     * cancel manual quote.
    */
    public void processCancelManualQuote(ManualQuote manualQuote, char cancelReason) throws DataValidationException, SystemException;
    
    public void processHALOOrders(List<Order> orders, OrderBook orderBook, HALStruct halStruct, short subType) throws SystemException, DataValidationException;
    
    public void processReCOA(Integer productKey, TradingClass tradingClass, Side side) throws DataValidationException, SystemException;
    
    public void doTradeAndSweepOrderProcessing(Tradable incomingTradable, List<Order> auctionResponses, List<Order> orderList, OrderBook orderBook, boolean isOrderFlashed, long auctionStartTime) throws DataValidationException, SystemException;
    
    public void doTradeAndSweepOrderProcessing(Tradable incomingTradable, List<Order> auctionResponses, Order anOrder, OrderBook orderBook, boolean isOrderFlashed, long auctionStartTime, AllExchangesBBO allExchangesBBO, HALStruct hal) throws DataValidationException, SystemException;
    
    public void doTradeAndSweepOrderProcessing(List<Order> auctionResponses, Order anOrder, OrderBook orderBook, boolean isOrderFlashed, long auctionStartTime) throws DataValidationException, SystemException;
    
    public void doTradeAndSweepOrderProcessing(List<Order> auctionResponses, Order anOrder, OrderBook orderBook, boolean isOrderFlashed, long auctionStartTime, HALStruct halStruct) throws DataValidationException, SystemException;
    
    public void processSweepTradable(List<List<SweepElement>> tradableSweepList, Order anOrder, OrderBook orderBook, Price tradeEndingPrice, long auctionStartTime, boolean sweepTrade) throws DataValidationException, SystemException;
    
    public void handleSweepRemainder(Order anOrder, OrderBook orderBook, boolean bookOrder, int orderQuantityAllowed, String handlingMessage) throws DataValidationException;
    
    public void processSALAuction(Order anOrder, OrderBook orderBook, HALStruct halStruct) throws DataValidationException, SystemException;
    
    public void processInternalSweepTradable(Tradable incomingTradable,  List<Order> auctionResponses, Order anOrder, OrderBook orderBook, HALStruct hal, Price tradeEndingPrice, long auctionStartTime) throws DataValidationException, SystemException;
    
    public void handleHALOrderAuction(Order anOrder, OrderBook orderBook, HALStruct halStruct, AuctionInternal auction) 
        throws SystemException, DataValidationException;
    
    public void handleAuctionWithIncomingQuoteUpdate(OrderBook orderBook, QuoteSide newBid, QuoteSide newAsk, String sessionName) throws DataValidationException;
    
    public void processSweepForReturnRemainder(Order anOrder) throws DataValidationException, SystemException;
    
    public void doNoSweepOrderProceesing(Order anOrder) throws DataValidationException, SystemException;
    
    public void doInternalSweepOrderProceesing(Order anOrder) throws DataValidationException, SystemException;
    
    public void doTradeAndSweepOrderProcessingForMQ(ManualQuote manualQuote, List<Order> auctionResponses, List<Order> orderList, OrderBook orderBook, boolean isOrderFlashed, long auctionStartTime) throws DataValidationException, SystemException;
    /* Sweep process for AIM with SWEEP request*/
    public void sweepProcessForAIMSweep(Order primaryOrder, Price tradeEndingPrice,OrderBook book) throws SystemException, DataValidationException;
    public void tradePrimaryOrderAgainstBook(InternalizationPair theInternalizationPair, OrderBook book, Price startingPrice)
    throws DataValidationException, SystemException;
    public void handleAIMSWEEP(Order anOrder, int shipQuantity) throws DataValidationException;
    void resetIndexHybridProperties(Order order);
    
    public void processSpreadOrderForLegTrade(Order spreadOrder, AuctionInternal auction) throws DataValidationException, SystemException;
    
    public void addToOrderBookInSlave(Order order)  throws DataValidationException;
    public void removeFromOrderBookInSlave(Order order, int p_unbookQty) throws DataValidationException;
    public void updateOrderBookInSlave(Order order, int bookQtyChange) throws DataValidationException;
    public void processRefreshBestBookInSlave(OrderBook book) throws DataValidationException;
    public void doExpireHALTriggerIfNeeded(Order incomingOrder, OrderBook book, Price expectedTradePrice) throws DataValidationException, SystemException;
    public boolean executeAuctionSALProcessing(Order anOrder, OrderBook orderBook, HALStruct halStruct, boolean checkContingency) throws DataValidationException, SystemException;
    public boolean processOnGoingAuction(AuctionInternal auction, Tradable incomingTradable, Price botrPrice) throws DataValidationException;
    public HALStruct handleQuoteLock(Order anOrder, OrderBook orderBook, HALStruct halStruct) throws DataValidationException, SystemException;
    public void processRegularTradable(Tradable newTradable, OrderBook book, Price tradeEndingPrice) throws DataValidationException, SystemException;
    public void doNewBOBSweepAndReturn(Order anOrder, OrderBook orderBook, Price tradeEndingPrice) throws DataValidationException, SystemException;
    public boolean doFishRequestProcessing(Order anOrder, OrderBook orderBook, Price autolinkPrice, int autolinkQuantity) throws DataValidationException, SystemException;
    public void doFishRemainderProcessing(Order anOrder) throws DataValidationException, SystemException;
    
    public void setPMMDetails(int spreadProduct, char spreadOrderSide, String[] preferredFirms)
        throws SystemException, DataValidationException;
    
    public void expireAllLegTimerBasedHoldupForAIMComplexOrder(TradingProduct tradingProduct)
        throws DataValidationException,SystemException;

    public void tradeWithNonQOnly(Order anOrder, OrderBook book, Price expectedTradePrice, boolean isTreatedLikeCustomerOnly, boolean nonContingency)
        throws DataValidationException, SystemException;
    public void processTSBRequest(OrderHandlingStruct orderStruct) 
        throws DataValidationException, SystemException;
    public boolean preAuctionSALProcessing(Order anOrder, OrderBook orderBook, HALStruct halStruct) throws DataValidationException, SystemException;
}
