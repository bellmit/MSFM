package com.cboe.interfaces.domain;

import java.util.ArrayList;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.AuctionHistoryStruct;
import com.cboe.idl.order.MarketDetailStruct;
import com.cboe.idl.order.OrderHandlingStruct;
import com.cboe.idl.sweepAutoLink.AutoLinkStruct;

public interface Order extends Tradable,OrderCommon
{
    public static final String CBSX_SESSION = "W_STOCK";
    
    /** Cancel id that translates to a SYSTEM cancel reason in the history */
    
    public static final String INTERNALIZED_ORDER_PREFIX = "A:AI";
    public static final String INTERNALIZED_ORDER_MECHANISM = "A:AIM";
    public static final String INTERNALIZED_ORDER_RETURN = "A:AIR";
    public static final String INTERNALIZED_ORDER_SWEEP = "A:AIS";
    public static final String FIRM_MATCH_ORDER_PREFIX = "A:RE";
    public static final String OPTOUT_FIRM_MATCH_ORDER_PREFIX = "A:RO";
    public static final String QUALIFIED_CONTINGENT_ORDER_PREFIX = "A:AIQ";
    // auction response order's optional data field will set as the following value
    // so that it can be used by CtmAdapter for sending auction response trade to CTM
    public static final String AUCTION_RESPONSE = "A:AR";
    /** Cancel id that translates to a SYSTEM cancel reason in the history */
  
    public static final String SYSTEM_LINK_ORDER_USER_ASSIGNED_ID = "System_Created_Link_Order";
	
    public void activate();
    //productKey is introduced to the method signature to accomodate the requirement of
    //individual leg cancel (from TPF)
	public int cancel(int productKey, int quantity, String userAssignedCancelId, 
            short cancelReason, boolean p_publishReportsUnconditionally)
	 throws DataValidationException;
    
  
    
    //Interface for adjusting order quantity when the cancel has been done in external system

    // 5 methods added for linkage:
	public FilledReportStruct linkageFill( int productKey, long tradeId, int totalQuantity, Price aPrice, ContraPartyStruct[] contras, String reportExtensions) throws DataValidationException;
	public int linkageCancel(int productKey, String userAssignedCancelId, short cancelReason);
	public int linkageOrderReject(int productKey, String userAssignedCancelId, short cancelReason );
//	public FilledReportStruct linkageFillReportReject( int productKey, long tradeId, int totalQuantity, Price aPrice, ContraPartyStruct[] contras, String reportExtensions ) throws DataValidationException;
	public boolean isLinkageOrder();

    // internal state of the order (BOOKED, ROUTED_AWAY, etc).  May include constants not normally exposed via OrderStruct.
    //
    public short getState();

    public int cancelReplace( int quantity, Order anOrder, String userAssignedCancelId ) throws DataValidationException;
    public int cancelReplaceWithoutNewOrderReport( int quantity, Order anOrder, String userAssignedCancelId ) throws DataValidationException;
	public int cancelWithoutReport( int quantity, boolean needToCreateHistory );
	public void expire();
    //Interface method for adjusting order quantity when the trade has been done in external system
//	public FilledReportStruct acceptExternalFill( int productKey, long tradeId, int totalQuantity, Price aPrice, ContraPartyStruct[] contras, String executingBroker) throws DataValidationException;
    public boolean legsHaveRemainingQuantity();
	public void bust( BustReportStruct bustInfo ) throws DataValidationException;
	public void bust( BustReportStruct bustInfo, TradeReport tradeReport ) throws DataValidationException;
	public void bust( BustReportStruct bustInfo, TradeReport tradeReport, boolean parTradeMMSideBust ) throws DataValidationException;
	public void reinstate( long tradeId, int quantity, Price aPrice ) throws DataValidationException, SystemException;
    public String getActiveSession();
	public char getTimeInForce();
	public int getCancelledQuantity();
	public int getBustedQuantity();
//	public void setBustedQuantity(int quantity);
	public Price getContingencyPrice();
	public int getContingencyVolume();
	public int getTradedQuantity();
	public OrderIdStruct getOrderId();
	public long getUniqueId();
	public int getClassKey();
//	public String getExecutingBroker();

	public int getOriginalQuantity();
	public int getPrevReservedQuantity(); //This is only used for reserve orders
	public int getPrevDisplayedQuantity();

//public String getOriginator();
//public String getOriginatorExchange();
//    public String getCmtaExchange();
//    public String getCmta();

    public ExchangeFirmStruct getCmtaFirm();
    public int getProductClassKey();
    public int getRemainingQuantity();
    public OrderHandlingStruct  getStruct();
    //public int getTransactionSequenceNumber();
    public int getUserKey();
    public String getUserAssignedId();
    public String getOptionalData();
    public String getAccount();
    public void inactivate();
    public boolean isActive();
    public boolean isExpired();
    public boolean isInactive();
    public boolean isRemoved();
    public boolean isWaiting();
    public int nothingDone() throws DataValidationException;
    public void publishNewOrder();
    public boolean isAuctionTrade();
    
//    public void setAdjustedContingencyPrice( Price newPrice);
    public void setOrder( OrderHandlingStruct anOrder ) throws SystemException, DataValidationException;
    public void update(OrderHandlingStruct newValues ) throws SystemException, DataValidationException;
//    public void validateNewValuesForUpdate (OrderHandlingStruct newValues) throws DataValidationException;

    public void setToWaiting();
    public long getOHSReceivedTime();

    public long getSourceReceivedTime();    // This is the time when source systems (eg TPF) received the order.

    // for stock sell short order
    public char getSideValue();

    public char getSource();

    public boolean hadNBBOProtection();
    public boolean hasTraded();
    public void overrideNBBOProtection(boolean aBoolean); //System and NBBO agent can override the customer's request for NBBO protection
    public short changeToState(short aNewState);
    public boolean isAssignedToNBBOAgent();
    public boolean isIPPExposing();

    //CD-Linkage methods.
    public void rejectFill(int tradedQuantity, PriceStruct tradePrice, String fillReportExtensions, short rejectReason) throws DataValidationException;
    public boolean isIncomingLinkageOrder();
    public void markForDropCopy(boolean mark);
    public boolean isMarkedForDropCopy();
    public int cancelWithDisposition(short disposition);
    public void routedToAwayExchange();
    public void cancelRequestReject(int productKey, int quantity, String userAssignedCancelId);


    /**
     * Action has been performed on this order that has been sent to NBBOAgent successfully
     */
    public void acceptedByNBBOAgent();
    /**
     * Cancel the quantity for cancel request
     */
    public int cancelReplace( int quantity, Order anOrder, String userAssignedCancelId, long cancelRequestId) throws DataValidationException;

    /**
     * Cancel request action has been performed on this order
     */
    public void cancelReplaceRequested( int quantity, Order anOrderReplacement, String userAssignedCancelId, long cancelRequestId);

    /**
     * this new method is introduced to handle the cancel request from NBBO agent.
     */
	public int cancel(int productKey, int quantity, String userAssignedCancelId, 
            short cancelReason, long cancelRequestId, 
            boolean p_publishReportsUnconditionally )
	 throws DataValidationException;

    /**
     * action has performed on this order on a cancel request.
     */
    public void cancelRequested(int productKey, int quantity, String userAssignedCancelId, short cancelReason, long cancelRequestId);

    /**
     * Return the handling instruction associated with this tradable
     */
    public HandlingInstruction getHandlingInstruction();

    /**
     * Return true if the order has been routed from the current system away to other system
     */
    public boolean hasRoutedAway();

    /**
     * The order was returned to source (no longer in our book) with the reason.
     */
    public void returnedWithReason(short returnReason);

    public int cancel(int productKey, int quantity, int tlcQuantity, 
            String userAssignedCancelId, short cancelReason, 
            long cancelRequestId, boolean p_publishReportsUnconditionally)
    throws DataValidationException;
    /**
     * Currently, these cancel request pending is for quote trigger handling
     */
    public void setCancelRequestPending(String userAssignedCancelId, int cancelQuantity);
    public void setCancelReplaceRequestPending(String userAssignedCancelId, int cancelQuantity, Order newOrder) throws DataValidationException;
    public Order getCancelReplaceNewOrder();
    public boolean isCancelRequestPending();
    public void pendingCancelProcessed();
    public String getPendingCancelRequestUserAssignedId();
    public int getPendingCancelQuantity();
    /**
     * clear the handling instruction associated with the order
     */
    public void clearHandlingInstruction();

    public void markCancelReason(short cancelReason);

    public boolean allowCancelInQuoteTrigger();

    public short getMarkedCancelReason();

    public String getExtensions();

    /**
     * Check if the order is a firm match order
     */
    public boolean isFirmMatchOrder();

    /**
     * Check if the order is an internalized order
     */
    public boolean isInternalizedOrder();

    public void setShipQuantity(int shipQuantity);
    public int getShipQuantity();
    
    public boolean allowTradeAndShip();
    public void setFillType(short fillContext);

    public boolean isSalable();

    //used to identify whether the order is already SALAuctioned.
    public void markAuctionStarted(boolean flag);
    public boolean isOrderAuctioned();

    public void setExtensions(String aValue);
//    public void setQuantity( int newQuantity);
  //  public void setOrsId(String aValue);
    public void setHandlingInstruction(HandlingInstruction anInstruction);     

    // This is to be used to retrieve order handling instruction persisted in database
    // when recovering orders during BC failover or startup
    public String getOrderHandlingInstruction();

	public boolean isTimeContingent();
    
    public boolean validateNbboFlashThenCancelConditions();

    
     //This is only used by order implementations
    public void postBookUpdateProcessing( Order self, OrderBook book )throws DataValidationException;

    public void setCrossingIndicator(boolean aValue);
    public boolean getCrossingIndicator();
    public boolean isCrossOrder();
	
	public boolean isReceivedForTheFirstTimeFromTPF ();
    public void receivedSecondOrMoreTimesFromTPF();
    public String getOrderIdString();
    public char getCoverage();
    public char getPositionEffect();
    public String[] getSessionNames();
    public OrderContingencyStruct getContingencyStruct();
    
    // Return Cancel and CancelRe orders with reason over IOSC.

    public void returnOrderWithReasonAndBookDepth(BookDepthStruct bookDepth, short returnReason);
    public void returnCancelWithReason(CancelRequestStruct cancelRequest, short returnReason);
    public void returnCancelReplaceWithReason(Order originalOrder, CancelRequestStruct cancelRequest, short returnReason);
    public void returnCancelReplaceWithReason(CancelRequestStruct cancelRequest, short returnReason);
    public void handleReturnCancelReplaceWithReason(Order replacedNewOrder, CancelRequestStruct cancelRequest, short returnReason);
    public void returnCancelReplaceWithReasonAndBookDepth(Order originalOrder, CancelRequestStruct cancelRequest, BookDepthStruct bookDepth, short returnReason);
    public void returnCancelReplaceWithReasonAndBookDepth(CancelRequestStruct cancelRequest, BookDepthStruct bookDepth, short returnReason);
    public void handleReturnCancelReplaceWithReasonAndBookDepth(Order replacedNewOrder, CancelRequestStruct cancelRequest, BookDepthStruct bookDepth, short returnReason);
    public OrderStruct getOrderStruct();
    public int getBookedQuantity();
    public int getTransactionSequenceNumber();
    public void setShipQuantityWithoutPublishing(int aShipQuantity);
    public FilledReportStruct[] getFilledReports();
    public void setMOCBeforeActivationCheck(boolean flag);
    public boolean mocBeforeActivationCheck();
    public void setOddLotFlashedQuantity(int flashedQuantity);
    public int getOddLotFlashedQuantity();
     
    public boolean needPublishBookedEvent();
    public void setNeedPublishBookedEvent(boolean p_needPublishBookedEvent);
    
    public boolean isComplexOrder();
    public boolean isExpressOrder();
    public boolean isInboundISOEnabled();
    
    public void publishAuctionEvent(AuctionHistoryStruct auctionInfo);
    public void publishAutoLink(AutoLinkStruct autoLinkStruct, MarketDetailStruct[] orderHistoryStruct);
    
    public boolean isDeltaNeutral() throws NotFoundException, SystemException;
    public int getPrimitiveProductKey();
    public short getProductType();
    public MarketDetailStruct[] buildMarketDetailStructs(); 
    public boolean isSweepTrade();
    public void setSweepTrade(boolean value);
    public void publishIncrementBookQuantityAudit(int addedQuantity);
    public void publishCancelReasonAudit(String cancelReasonDescription);
    
    //Checks for the New HAL Auction.
    public boolean isNewHALEnabled();
    public void setDirectedAIMOrderAndNotMatched(boolean notMatched);
    public boolean isDirectedAIMOrderMatched();
    
    public ArrayList<Integer> getDAIMedUserList();
    public void setDAIMedUserList(ArrayList<Integer> dAIMUserList);
    public void publishDirectedAIMNotification(AuctionHistoryStruct auctionInfo);
    
    public void cancelAutoLinkTimer();
    public AutoLinkedOrderExpireTimerTask getAutoLinkedExpireTimerTask();
    public void setAutoLinkedExpireTimerTask(AutoLinkedOrderExpireTimerTask p_autoLinkedExpireTimerTask);
    public double getBookableOrderMarketLimit();
    public void setBookableOrderMarketLimit(double p_bookableOrderMarketLimit);
    public LegOrderDetail[] getLegOrderDetails();
    public void setCancelledQuantity(int aValue);
    public void publishBustReinstateReport(long tradeId, int quantity);
    
    public boolean isQCTPrimaryOrder();
    public boolean isCustomerTypeForBobClasses();
    
    public ManualQuote getManualQuote();
    public void setManulQuote(ManualQuote theManualQuote);
    public boolean isManualQuote();
    
    public Price getMarketOrderConvertedPrice();
    public void setMarketOrderConvertedPrice(Price p_marketOrderconvertedPrice);
    public void forceOrderSync();
    
    public void incrementLegTradeAttemptCount();
    public void resetLegTradeAttemptCount();
    public boolean canAttemptLegTrade();
    
    public void setTotalPrice(double newTotal);
    public void setTotalSessionPrice(double newTotal);
    public void setTotalSessionTradedQuantity(int newTotal);
    public void setTotalSessionCancelledQuantity(int newTotal);
    public void setUserContingencyType(short contingencyType);
    
    public boolean isLightOrder();
    
    public boolean isTBForCOB();
    public boolean isTBAtLegBBO();
    public void setIsTBAtLegBBO(boolean atLegBBO);
    public boolean isStrategy();
    public void setBillingCode(char billingCode);
    public char getBillingCode();
    public boolean isOptOutFirmMatchOrder();
    public Price getAutoMatchWithLimitPrice();
    
    public TradingProduct getTradingProduct() throws NotFoundException;
}
