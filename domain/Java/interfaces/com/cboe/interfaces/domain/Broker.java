package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
// Source file: com/cboe/interfaces/businessServices/Broker.java
//
// PACKAGE: com.cboe.interfaces.businessServices;
//
// ------------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
import java.util.List;
import java.util.TimerTask;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiQuote.QuoteStructV3;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.OrderHandlingStruct;
import com.cboe.infrastructureServices.queue.QueueException;

/**
 * Broker is an interface to performs the actual work of brokering orders.
 *
 * @version 3a_Pre.2
 * @author David Wegener
 * @author John Wickberg
 */
public interface Broker{

/**
  * @return Returns the trading class which this broker handles.
  */
public TradingClass getTradingClass();

/**
 * Returns the cached Trading Product Home.
 * @return
 */
public TradingProductHome getTradingProductHome();

/**
 * Accepts a <code>BrokerCommand</code> to be processed.
 *
 * @param aCommand		command to be processed
 * @param ignoreQueueLimit indicator on if the queue limit will be checked
 * @exception 				NotAcceptedException if the command
 * 							cannot be accepted.
 * @since Increment 3a
 */
public void acceptCommand(TradingClassCommand aCommand, boolean ignoreQueueLimit) throws NotAcceptedException;

/**
 * Accepts and processes a cancel for an order.
 *
 * @param anOrder						order to be cancelled
 * @param quantity 						quantity to cancel.
 * @param cancelType    the cancel type of the request
 * @exception 				NotAcceptedException if cancel cannot be accepted.
 *
 */
public boolean acceptCancel(Order anOrder, int quantity, int cancelType, 
        String userAssignedCancelId, boolean p_publishReportsUnconditionally) throws NotAcceptedException, DataValidationException;

/**
 * Accepts and processes a cancel response for an order.
 *
 * @param anOrder						order to be cancelled
 * @param quantity 						quantity to cancel.
 * @param cancelType                    cancelType for the cancel.
 * @param userAssignedCancelId          user assigned cancel request id
 * @param cacelRequestId                     cboe assigned cancel request id
 * @exception NotAcceptedException if the <code>Order</code> cannot be accepted in the current state.
 * @exception DataValidationException if the product cannot be found for the <code>Order</code>
 */
public void acceptCancelResponse(Order anOrder, int quantity, int cancelType, String userAssignedCancelId, long cacelRequestId) throws NotAcceptedException, DataValidationException;

/**
 * Cancels a <code>Quote</code>
 *
 * @param currentQuote 						quote to be cancelled
 * @exception NotAcceptedException 	if the <code>Quote</code> cannot
 *				be accepted in the current state
 * @exception DataValidationException if the product for the <code>Quote</code> cannot be found
 */
void acceptCancel(Quote currentQuote) throws NotAcceptedException, DataValidationException;
/**
 * Cancels an array of <code>Quote</code>'s for the same product class.
 *
 * @param quotes 						quotes to be cancelled
 * @exception NotAcceptedException if no quotes being cancelled are accepted.
 * @exception DataValidationException if no strategy can be found for quotes
 */
void acceptCancelQuotes(Quote[] quotes) throws NotAcceptedException, DataValidationException;
/**
 * Cancels an array of <code>Quote</code>'s for the same product class.
 *
 * @param quotes 						quotes to be cancelled
 * @param quoteCancelInfo   Additional info for quotes to be canceled
 * @exception NotAcceptedException if no quotes being cancelled are accepted.
 * @exception DataValidationException if no strategy can be found for quotes
 */
void acceptCancelQuotes(Quote[] quotes, QuoteCancelInfo quoteCancelInfo) throws NotAcceptedException, DataValidationException;

/**
 * Cancel Quotes for User by Class.
 * @param userId
 * @param clasSKey
 * @param quoteCancelInfo
 * @throws NotAcceptedException
 * @throws DataValidationException
 */
void acceptCancelQuotes(String userId, int classKey, QuoteCancelInfo quoteCancelInfo) throws NotAcceptedException, DataValidationException;

/**
 * Accepts and processes a cancel replace for an order.
 *
 * @param originalOrder 				order to be cancelled
 * @param quantity 						quantity to cancel depends on the cancelType
 * @param cancelType                    cancelType on the quantity to be canceled
 * @param newOrder 						order to replace the original order with
 * @exception NotAcceptedException	if cancel replace cannot be accepted
 * 				in the current state
 * @exception DataValidationException if the product cannot be found for the <code>Order</code>
 */
public void acceptCancelReplace(Order originalOrder, int quantity, int cancelType, Order newOrder, String userAssignedCancelId) throws NotAcceptedException, DataValidationException;

/**
 * Accepts and processes a cancel replace response for an order for a cancel request id.
 *
 * @param originalOrder 				order to be cancelled
 * @param quantity 						quantity to cancel depends on the cancelType
 * @param cancelType                    cancelType on the quantity to be canceled
 * @param newOrder 						order to replace the original order with
 * @param userAssignedCancelId          user assigned cancel request id
 * @param cancelRequestId               cboe assigned cancel request id
 * @exception NotAcceptedException	if cancel replace cannot be accepted
 * 				in the current state
 * @exception DataValidationException if the product cannot be found for the <code>Order</code>
 */
public void acceptCancelReplaceResponse(Order originalOrder, int quantity, int cancelType, Order newOrder, String userAssignedCancelId, long cancelRequestId) throws NotAcceptedException, DataValidationException;

/**
 * Accept and process a <code>Cross</code>
 *
 * @param aCross Cross to be executed.
 * @exception NotAcceptedException if the <code>Cross</code> is not accepted
 * @exception DataValidationException if the <code>Cross</code> cannot be
 * accepted in the current state
 */
void acceptCross(Cross aCross) throws NotAcceptedException, DataValidationException;
/**
 * Accepts and processes an indication that a strategy is now leaving the hold
 * condition.
 *
 * @param product			trading product that is leaving hold.
 */
void acceptEndingHold(TradingProduct product);
/**
 * Accepts and processes an indication that a product is now
 * in opening rotation.
 *
 * @param tradingProducts		<code>TradingProduct>s in opening rotation
 * @since Increment 3a
 */
public void acceptOpeningRotation(List tradingProducts);
/**
 * Accepts and processes an indication that a product is now
 * in preOpening.
 *
 * @param tradingProducts		<code>TradingProduct>s in pre-open
 * @since HOpE
 */
public void acceptPreOpen(List tradingProducts);
/**
 * Accept and process an <code>Order</code>
 *
 * @param anOrder 						order to be traded.
 * @exception NotAcceptedException 	if the <code>Order</code> is not currently
 *														acceptable.
 * @exception DataValidationException 	if the <code>Order</code> is for an unknow product
 * @exception NotAcceptedException if the <code>Order</code> cannot
 *				be accepted in the current state
 */
public void acceptOrder(Order anOrder) throws NotAcceptedException, DataValidationException;

/**
 * Accept and process an <code>Quote</code>
 *
 * @param quotes 						quotes to be traded.
 */
void acceptQuoteBlock(QuoteBlockIterator quotes) throws NotAcceptedException;
/**
 * Accepts  and processes an <code>Order</code> update.
 *
 * @param currentOrder 		order to be updated
 * @param updatedOrder 		OrderHandlingStruct containing the updates to be applied to
 *							thie order
 * @exception 				DataValidationException if product for order not found
 * @exception 				NotAcceptedException if the update
 *							is not allowed in the current state
 */
void acceptUpdate(Order currentOrder, OrderHandlingStruct updatedOrder ) throws NotAcceptedException, DataValidationException;
/**
 * Tests to see if quotes can be accepted for product.
 *
 * @param productKey key of product for quote
 * @return true if current product state allows quotes to be entered.
 */
boolean areQuotesAccepted(int productKey);

/**
 * Tests to see if quotes can be accepted for a Trading Product.
 *
 * @param tradingProduct tradingProduct for quote
 * @return true if current product state allows quotes to be entered.
 */
boolean areQuotesAccepted(TradingProduct tradingProduct);

/**
 * Creates an RFQ for a product.
 *
 * @param productKey RFQ product
 * @param rfqQuantity quantity being requested
 */
void createRFQ(Integer productKey, int rfqQuantity);
/**
 * Gets the processor for this broker.
 *
 * @return a processor
 */
BrokerProcessor getProcessor();
/**
 * @param order       order to be reinstated
 * @param quantity    quantity to be reinstated
 * @exception                 SystemException
 * @exception                 DataValidationException
 */
void processReinstateOrder(Order order, long tradeId, int quantity ) throws SystemException, DataValidationException, NotAcceptedException;
/**
 * When the best book of a leg of a spread product changes, the broker should try to trade the
 * spread orders in the book against the regular orders of legs of the spread. This method is
 * designed to handle such requirement.
 *
 * For a broker of a non-spread product, this method is not going to be used.
 */
void acceptLegBestBookChange(TradingProduct product);
/**
 * Accept the reroute of held order from nbbo agent
 */
void acceptReroute(Order anOrder, boolean nbboProtection) throws DataValidationException, NotAcceptedException;
/**
 * Return the configured NBBO derived order exposing time
 */
int getNBBODerivedOrderExposingTime();

/**
 * Accepts the NBBO agent's order to satisfy the customer order for which Satisfaction order was created.
 * @param refSatisfactionOrder
 * @param agentOrder
 * @throws DataValidationException
 * @throws NotAcceptedException
 */
void acceptCustomerOrderSatisfy(Order refSatisfactionOrder, Order agentOrder) throws DataValidationException, NotAcceptedException;

/**
 * Fill the satisfaction order equally among all the logged in market maker having quote in the class.
 * @param satisfactionOrder
 * @param crowdQuantity
 * @param cancelRemaining
 */
void acceptSatisfactionOrderInCrowdFill(Order satisfactionOrder, int crowdQuantity, boolean cancelRemaining, short disposition)throws DataValidationException, NotAcceptedException;

/**
 * Reject the Incoming linkage order at the request of NBBO agent.
  * @param anOrder
 * @param resolution
 */
void acceptNewOrderReject(Order anOrder, short resolution) throws DataValidationException, NotAcceptedException;

/**
 * Gets multiplier to use for held market orders.
 */
double getHeldMarketOrderMultiplier();

/**
 *  Lock the OrderBook
 *
 *  @param product tradingProduct
 *
 *  @exception NotAcceptedException
 *  @exception SystemException
 *
 *  @ author Sandip Chatterjee
 */
public void acceptLockOrderBook(TradingProduct product) throws SystemException, NotAcceptedException, DataValidationException;

/**
 *  UnLock the OrderBook
 *
 *  @param product tradingProduct
 *
 *  @exception NotAcceptedException
 *  @exception SystemException
 *
 *  @ author Sandip Chatterjee
 */
public void acceptUnLockOrderBook(TradingProduct product) throws SystemException, NotAcceptedException, DataValidationException;

/**
 *  Reroute the Order From the OrderBook to the NBBOAgent
 *
 *  @param currentOrder Order
 *  @param nbboProtectionFlag boolean
 *
 *  @exception NotAcceptedException
 *  @exception SystemException
 *
 *  @ author Sandip Chatterjee
 */
public void acceptRerouteBookedOrderToHeldOrder(Order currentOrder,boolean nbboProtectionFlag)
throws  SystemException,CommunicationException,DataValidationException,TransactionFailedException,NotAcceptedException;

/**
 *  Process the request to change the state of the OrderBook
 *
 *  @param currentOrderBook OrderBook
 *  @param toLock boolean - Whether to lock or unlock the OrderBook
 *
 *  @exception NotAcceptedException
 *  @exception SystemException
 *
 *  @ author Sandip Chatterjee
 */
public void processOrderBookStateChangeRequest(OrderBook currentOrderBook,boolean toLock)
throws  SystemException,DataValidationException,TransactionFailedException,NotAcceptedException;


/**
* Process the request to store the Command in the Held Order Queue
*
* @param tcCommand TradingClassCommand
*
* @exception NotAcceptedException
* @exception SystemException
*
* @ author Sandip Chatterjee
*/
public void acceptCommandInLockedState(TradingClassCommand tcCommand)
throws  SystemException,NotAcceptedException;

/**
* Process the request to clear the held order queue during the market open
*
* @exception NotAcceptedException
* @exception SystemException
*
* @ author Sandip Chatterjee
*/
public void processMarketOpenRelatedHeldOrderQueue()
throws  SystemException,NotAcceptedException;

/**
* Open the product at a give price.
*
* @param openingPrice The price at which the product will be opened.
* @param productKey Information on the product that is being moved
*
* @exception SystemException
* @exception CommunicationException
* @exception AuthorizationException
* @exception DataValidationException
* @exception TransactionFailedException
* @exception NotAcceptedException
* @ author Sandip Chatterjee
*/
public void acceptOpeningPriceForProduct(PriceStruct openingPrice,int productKey)
throws SystemException,CommunicationException,AuthorizationException,TransactionFailedException,NotAcceptedException,DataValidationException;

/**
* Tests to see if quotes can be accepted for product. Passing the QuoteStructas an additional
* parameter
*
* @param productKey key of product for quote
* @param aQuoteStruct key for the User related info.
* @return true if current product state allows quotes to be entered.
* @ author Sandip Chatterjee
*/
boolean areQuotesAccepted(int productKey,QuoteStructV3 aQuoteStruct);

/**
 * To clear the held order queue that stores commands before the market open
 * @throws QueueException
 */
void resetHeldOrderQueue() throws QueueException;

/**
 * To display the number of orders in the held order queue.
*/
public int displayHeldOrderQueueCount() throws QueueException;

/**
 * Return if checking firm quote requirement is met for SAL auction
 */
public boolean checkFirmQuoteForSAL();

/**
 * Return if checking firm quote requirement is met for BOB SAL auction
 */
public boolean checkFirmQuoteForBOBSAL();

/**
 * Schedule a <code>TimerTask</code> with the <code>Timer</code> in broker.
 * @param task
 * @param delay
 */
public void scheduleTimerTask(int classKey, TimerTask task, long delay);

public int getMatchingCrossOrderTimeout();
/**
 * Accept manual quote
 * @param manualQuote
 * @throws NotAcceptedException
 * @throws DataValidationException
 */
public void acceptManualQuote(ManualQuote manualQuote) throws NotAcceptedException, DataValidationException;

/**
 * accept request to cancel manual quote.
 * @param manualQuote
 * @throws NotAcceptedException
 * @throws DataValidationException
 */
public void acceptCancelManualQuote(ManualQuote manualQuote) throws NotAcceptedException, DataValidationException;
public void acceptCancelManualQuote(ManualQuote manualQuote, char cancelReason) throws NotAcceptedException, DataValidationException;

public void rollbackTX(int prodKey) throws Exception;

public void acceptAutoLinkReturnedRemainder(Order underlyingOrder, int p_unfilledShippedQuantity,
        int p_filledShippedQuantity, boolean resweepEligible, long taskId) throws DataValidationException, NotAcceptedException, SystemException;       
public void refreshBookDepth();
public void refreshBookDepth(TradingProduct[] tps, int startIndex, int maxProductsPerCommand, Object callback);
public void refreshBookDepth(TradingProduct tp, Object callback);

public void reBuildBook(TradingProduct aProduct, boolean isRecoverOrders);
public void reBuildBook(TradingClass aClass, boolean isRecoverOrders);

public void createOrderBook(TradingProduct[] tps, int startIndex, int maxProductsPerCommand, Object callback);
public void createOrderBook(TradingProduct tp, Object callback); 
/**
 * This method will be called if there is a pending quote cancel
 * 
 * @param quotes
 * @param quoteCancelInfo
 * @throws NotAcceptedException
 */
public void cancelPendingQuotes(TradingProduct pendingQuoteCancelProduct, QuoteCancelInfo quoteCancelInfo) throws NotAcceptedException ;

public void rollbackTransactionAndRebuildBook(int prodKey) throws Exception;

public QuoteHome getQuoteHome();

public void acceptSpreadOrderForLegTrading(Order spreadOrder, AuctionInternal auction) throws NotAcceptedException, DataValidationException;
public void acceptAddToOrderBookInSlave(Order order, int p_bookQty) throws NotAcceptedException;
public void acceptRemoveFromOrderBookInSlave(Order order, int p_unbookQty) throws NotAcceptedException;
public void acceptUpdateOrderBookInSlave(Order order, int bookQtyChange) throws NotAcceptedException;
public void acceptRerouteOrderDuringFailover(Order p_order) throws NotAcceptedException, SystemException, DataValidationException;
public void acceptLinkedAwayOrderDuringFailover(Order p_underlyingOrder) throws NotAcceptedException, SystemException, DataValidationException;
public void acceptTSBRequest(OrderHandlingStruct orderStruct) throws NotAcceptedException, SystemException, DataValidationException;

}
