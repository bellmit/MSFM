/**
 * 
 */
package com.cboe.application.order.common;

import com.cboe.application.order.OrderQueryCache;
import com.cboe.application.order.OrderQueryCacheFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.IOrderAckConstraints;
import com.cboe.client.util.CollectionHelper;
import com.cboe.domain.util.CompleteOrderIdStructContainer;
import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.domain.util.GroupOrderStructContainer;
import com.cboe.domain.util.NoPrice;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiConstants.OrderStates;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiConstants.ContingencyTypes;
import com.cboe.idl.cmiConstants.Sources;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelEvent;

/**
 * Some utility methods used for generating and publishing order status NEW messages.  
 * @author Gijo Joseph
 *
 */
public class UserOrderServiceUtil 
{
	// GENERATE_NEW_MSG property decides whether to generate the order 
	// status NEW message locally or not. Default is true (i.e. generates NEW)
    public static final String GENERATE_NEW_MSG = "generateNewMsg"; 
    public static final String GENERATE_NEW_MSG_DEFAULT = "true";
    private static boolean generateNewMsg = true;

    // DISABLE_NEW_ACK property decides whether CAS will ACK to the server after  
	// successful delivery of an order status NEW message. Default is true (i.e. DO NOT ACK)
    public static final String DISABLE_NEW_ACK = "disableNewAck";
    public static final String DISABLE_NEW_ACK_DEFAULT = "true";
    private static boolean disableNewAck = true;

    public static final String EXPRESS_ORDER_SESSIONS = "expressOrderSessions";
    public static final String EXPRESS_ORDER_SESSIONS_DEFAULT = "";
    private static String[] expoSessions = com.cboe.client.util.CollectionHelper.EMPTY_String_ARRAY;


    // The minimum time an order will stay in CAS cache after it has been filled/cancelled.
    public static final String FINISHED_ORDER_LIFETIME = "finishedOrderMinLifeTime";
    public static final String FINISHED_ORDER_LIFETIME_DEFAULT = "30000";
    private static long finishedOrderMinLifeTime = 30 * 1000L; // 30sec default

    // flag to turn OFF or ON the checking of the finished order map. default ON.
    public static final String CHECK_FINISHED_MAP = "checkFinishedMap";
    public static final String CHECK_FINISHED_MAP_DEFAULT = "true";
    private static boolean checkFinishedMap = true;

    // flag to turn OFF or ON the checking of the NEW order queue. default ON.
    public static final String CHECK_NEW_QUEUE = "checkNewQueue";
    public static final String CHECK_NEW_QUEUE_DEFAULT = "true";
    private static boolean checkNewQueue = true;

    // flag to turn OFF or ON the error code NO_WORKING_ORDER as opposed to INVALID_ORDER_ID. default ON.
    public static final String USE_NO_WORKING_ORDER = "useNoWorkingOrder";
    public static final String USE_NO_WORKING_ORDER_DEFAULT = "true";
    private static boolean useNoWorkingOrder = true;
    private static PriceStruct zeroValuedPrice = new PriceStruct( PriceTypes.VALUED, 0, 0 );
    public final static String CAS_GENERATED_CANCEL_REPORT ="LT6-2";
    static
    {
	    try
	    {
	        generateNewMsg = Boolean.parseBoolean(System.getProperty(GENERATE_NEW_MSG, GENERATE_NEW_MSG_DEFAULT)); // default is true
	    }
	    catch(Exception e) 
	    {
	    	Log.exception(e);
	    }
	    Log.information(GENERATE_NEW_MSG + " value set to " + generateNewMsg);     	
	    try
	    {
	    	disableNewAck = Boolean.parseBoolean(System.getProperty(DISABLE_NEW_ACK, DISABLE_NEW_ACK_DEFAULT)); // default is true
	    }
	    catch(Exception e) 
	    {
	    	Log.exception(e);
	    }
	    Log.information(DISABLE_NEW_ACK + " value set to " + disableNewAck);
        try
        {
            String sessionList = System.getProperty(EXPRESS_ORDER_SESSIONS, EXPRESS_ORDER_SESSIONS_DEFAULT);
            if (sessionList != null)
            {
                expoSessions = sessionList.split(",");
            }
            Log.information(EXPRESS_ORDER_SESSIONS + " value set to " + sessionList);
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
        try
        {
            finishedOrderMinLifeTime = Long.parseLong(System.getProperty(FINISHED_ORDER_LIFETIME, FINISHED_ORDER_LIFETIME_DEFAULT));
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
        Log.information(FINISHED_ORDER_LIFETIME + " value set to " + finishedOrderMinLifeTime);
        try
        {
            checkFinishedMap = Boolean.parseBoolean(System.getProperty(CHECK_FINISHED_MAP, CHECK_FINISHED_MAP_DEFAULT)); // default is true
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
        Log.information(CHECK_FINISHED_MAP + " value set to " + checkFinishedMap);
        try
        {
            checkNewQueue = Boolean.parseBoolean(System.getProperty(CHECK_NEW_QUEUE, CHECK_NEW_QUEUE_DEFAULT)); // default is true
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
        Log.information(CHECK_NEW_QUEUE + " value set to " + checkNewQueue);
        try
        {
            useNoWorkingOrder = Boolean.parseBoolean(System.getProperty(USE_NO_WORKING_ORDER, USE_NO_WORKING_ORDER_DEFAULT)); // default is true
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
        Log.information(USE_NO_WORKING_ORDER + " value set to " + useNoWorkingOrder);
    }
    
    private static ProductQueryServiceAdapter pqsAdapter = null;

    private static short getOrderState(OrderStruct order)
    {
        short state = OrderStates.BOOKED;
        if (order.contingency.type == ContingencyTypes.STP
                || order.contingency.type == ContingencyTypes.STP_LOSS
                || order.contingency.type == ContingencyTypes.STP_LIMIT)
        {
            state = OrderStates.WAITING;
        }
        return state;
    }

    /*
	 * Initialize the order status NEW message. 
	 */
	public static GroupOrderStructContainer createNewMessage(OrderStruct newOrder)
	{
        // As per Keval on 11/20/6, the statusChange that server sends is always "NEW"  
        // (i.e. 7, statusupdatereasons, cmiConstants.idl) irrespective of order type.
        short statusChange = StatusUpdateReasons.NEW; 
        int[] groups = com.cboe.client.util.CollectionHelper.EMPTY_int_ARRAY; // No value set since CAS does not use it.

        return new GroupOrderStructContainer( groups, statusChange, processNewOrder(newOrder) );
	}
	
	public static boolean isCasGeneratedCancellReport(String isLT6)
    {
        return CAS_GENERATED_CANCEL_REPORT.equals(isLT6);
    }

    /*
	 * Process the new order.
	 */
	public static OrderStruct processNewOrder(OrderStruct newOrder)
	{
        newOrder.transactionSequenceNumber= 1; // seq num for a NEW message is always 1.
        // As per Tom, currently an order trades only in one session.
        // If we ever allow an order to belong to multiple sessions,
        // this may need to be changed.
        newOrder.activeSession = newOrder.sessionNames[0];
        newOrder.leavesQuantity = newOrder.originalQuantity; // no fills yet.
        newOrder.state = getOrderState(newOrder);

        newOrder.sessionTradedQuantity = 0;
        newOrder.tradedQuantity = 0;
        newOrder.cancelledQuantity = 0;

        // Initializing the averagePrice and sessionAveragePrice
        // to zero price for all NEW messages (including Cancel-Replaces),
        // as confirmed by Ravi Vazirani.
        newOrder.averagePrice = zeroValuedPrice;
        newOrder.sessionAveragePrice = zeroValuedPrice;

        // initialize the optional fields if they contain nulls
        if (newOrder.optionalData == null)
        	newOrder.optionalData = "";
        if (newOrder.userAssignedId == null)
        	newOrder.userAssignedId = "";

        return newOrder;
	}

    /**
     * Get the instance of the ProductQueryServiceAdapter.
     */
    private static ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if (pqsAdapter == null)
        {
            pqsAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqsAdapter;
    }

	/*
	 * Gets the strategy product defintion for the given product key
	 */
	public static StrategyStruct getStrategyByProductKey(int productKey) 
			throws SystemException, CommunicationException, AuthorizationException, DataValidationException 
	{
		try 
		{
			return getProductQueryServiceAdapter().getStrategyByKey(productKey);
		}
		catch (NotFoundException nfe)
		{
	        throw ExceptionBuilder.dataValidationException(
	                "Product for strategy order could not be found: " +
	                        nfe.details.message, DataValidationCodes.INVALID_STRATEGY);
		}
	}
	
	/*
	 * Create default LegOrderEntryStruct's for a strategy order. 
	 */
	public static LegOrderEntryStruct[] createDefaultLegs(OrderStruct strategyOrder) 
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException 
	{
		StrategyStruct product = getStrategyByProductKey(strategyOrder.productKey);
		return createDefaultLegs(strategyOrder, product);
	}

	/*
	 * Create default LegOrderEntryStruct's for a strategy order. 
	 */
	private static LegOrderEntryStruct[] createDefaultLegs(OrderStruct strategyOrder, StrategyStruct product) 
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException 
	{
	    if (Log.isDebugOn()) {
	        Log.debug("Creating default legs for order " + getOrderIdString(strategyOrder.orderId));
	    }
	    LegOrderEntryStruct[] legEntries = new LegOrderEntryStruct[product.strategyLegs.length];
	    PriceStruct noPrice = new NoPrice().toStruct();
	    for (int i = 0; i < product.strategyLegs.length; i++) {
	        legEntries[i] = new LegOrderEntryStruct();
	        legEntries[i].productKey = product.strategyLegs[i].product;
	        legEntries[i].clearingFirm = new ExchangeFirmStruct("", "");
	        legEntries[i].coverage = strategyOrder.coverage;
	        legEntries[i].mustUsePrice = noPrice;
	        legEntries[i].positionEffect = strategyOrder.positionEffect;
	    }
	    return legEntries;
	}

    /*
	 * Create default LegOrderEntryStructV2's for a strategy order.
	 */
	public static LegOrderEntryStructV2[] createDefaultLegsV2(OrderStruct strategyOrder)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException
	{
		StrategyStruct product = getStrategyByProductKey(strategyOrder.productKey);
		return createDefaultLegsV2(strategyOrder, product);
	}

	/*
	 * Create default LegOrderEntryStructV2's for a strategy order.
	 */
	private static LegOrderEntryStructV2[] createDefaultLegsV2(OrderStruct strategyOrder, StrategyStruct product)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException
	{
	    if (Log.isDebugOn()) {
	        Log.debug("Creating default legs V2 for order " + getOrderIdString(strategyOrder.orderId));
	    }
	    LegOrderEntryStructV2[] legEntries = new LegOrderEntryStructV2[product.strategyLegs.length];
	    PriceStruct noPrice = new NoPrice().toStruct();
	    for (int i = 0; i < product.strategyLegs.length; i++) {
	        legEntries[i] = new LegOrderEntryStructV2();
	        legEntries[i].legOrderEntry.productKey = product.strategyLegs[i].product;
	        legEntries[i].legOrderEntry.clearingFirm = new ExchangeFirmStruct("", "");
	        legEntries[i].legOrderEntry.coverage = strategyOrder.coverage;
	        legEntries[i].legOrderEntry.mustUsePrice = noPrice;
	        legEntries[i].legOrderEntry.positionEffect = strategyOrder.positionEffect;
	    }
	    return legEntries;
	}

	private static StrategyLegStruct getMatchingStrategyLegStruct(StrategyLegStruct[] strategyLegs, int product) throws DataValidationException
	{
		for (StrategyLegStruct strLeg: strategyLegs)
		{
			if (strLeg.product == product)
				return strLeg;
		}
        throw ExceptionBuilder.dataValidationException(
                "No leg information found for product: = " + product, DataValidationCodes.INVALID_STRATEGY_LEG);		
	}
	
	/*
	 * Create LegOrderDetailStruct's from the strategy order, LegOrderEntryStructs and the StrategyLegStructs.
	 */
	public static LegOrderDetailStruct[] createLegOrderDetails(OrderStruct strategyOrder, LegOrderEntryStruct[] legEntryDetails, StrategyLegStruct[] strategyLegs) throws DataValidationException 
	{
	    if (Log.isDebugOn()) {
	        Log.debug("Creating default leg order detail for order " + getOrderIdString(strategyOrder.orderId) + " for product " +  strategyOrder.productKey);
	    }
	    if (legEntryDetails.length != strategyLegs.length)
	    {
	        throw ExceptionBuilder.dataValidationException(
	                "No. of legs in specified in LegOrderEntryStruct does not match with the no. of legs in the strategy definition! LegOrderEntryStruct length = " +
	                        legEntryDetails.length + " : Legs in Strategy = " + strategyLegs.length, DataValidationCodes.INVALID_STRATEGY_LEG);
	    }
	    LegOrderDetailStruct[] legDetails = new LegOrderDetailStruct[legEntryDetails.length];
	    for (int i = 0; i < legEntryDetails.length; i++) {
	    	StrategyLegStruct strLeg = getMatchingStrategyLegStruct(strategyLegs, legEntryDetails[i].productKey);
	    	legDetails[i] = new LegOrderDetailStruct();	    	
	        legDetails[i].productKey = legEntryDetails[i].productKey;
	        legDetails[i].clearingFirm = legEntryDetails[i].clearingFirm;
	        legDetails[i].coverage = legEntryDetails[i].coverage;
	        legDetails[i].mustUsePrice = legEntryDetails[i].mustUsePrice;
	        legDetails[i].positionEffect = legEntryDetails[i].positionEffect;
	        if (strategyOrder.side == Sides.OPPOSITE)
	        {
	        	legDetails[i].side = strLeg.side == Sides.BUY ? Sides.SELL : Sides.BUY;	        	
	        }
	        else
	        {
	        	legDetails[i].side = strLeg.side;
	        }
			legDetails[i].originalQuantity = strategyOrder.originalQuantity * strLeg.ratioQuantity;
			legDetails[i].leavesQuantity = legDetails[i].originalQuantity;
			legDetails[i].tradedQuantity = 0;
			legDetails[i].cancelledQuantity = 0;
	    }
	    return legDetails;
	}

    /*
	 * Create LegOrderDetailStruct's from the strategy order, LegOrderEntryStructV2 and the StrategyLegStructs.
	 */
	public static LegOrderDetailStruct[] createLegOrderDetails(OrderStruct strategyOrder, LegOrderEntryStructV2[] legEntryDetailsV2, StrategyLegStruct[] strategyLegs) throws DataValidationException
	{
	    if (Log.isDebugOn()) {
	        Log.debug("Creating default leg order detail for order " + getOrderIdString(strategyOrder.orderId) + " for product " +  strategyOrder.productKey);
	    }
	    if (legEntryDetailsV2.length != strategyLegs.length)
	    {
	        throw ExceptionBuilder.dataValidationException(
	                "No. of legs in specified in LegOrderEntryStructV2 does not match with the no. of legs in the strategy definition! LegOrderEntryStructV2 length = " +
	                        legEntryDetailsV2.length + " : Legs in Strategy = " + strategyLegs.length, DataValidationCodes.INVALID_STRATEGY_LEG);
	    }
	    LegOrderDetailStruct[] legDetails = new LegOrderDetailStruct[legEntryDetailsV2.length];
	    for (int i = 0; i < legEntryDetailsV2.length; i++) {
	    	StrategyLegStruct strLeg = getMatchingStrategyLegStruct(strategyLegs, legEntryDetailsV2[i].legOrderEntry.productKey);
	    	legDetails[i] = new LegOrderDetailStruct();
	        legDetails[i].productKey = legEntryDetailsV2[i].legOrderEntry.productKey;
	        legDetails[i].clearingFirm = legEntryDetailsV2[i].legOrderEntry.clearingFirm;
	        legDetails[i].coverage = legEntryDetailsV2[i].legOrderEntry.coverage;
	        legDetails[i].mustUsePrice = legEntryDetailsV2[i].legOrderEntry.mustUsePrice;
	        legDetails[i].positionEffect = legEntryDetailsV2[i].legOrderEntry.positionEffect;
            legDetails[i].side = legEntryDetailsV2[i].side;
            /**
             * Previous code would map the side from the
	        if (strategyOrder.side == Sides.OPPOSITE)
	        {
	        	legDetails[i].side = strLeg.side == Sides.BUY ? Sides.SELL : Sides.BUY;
	        }
	        else
	        {
	        	legDetails[i].side = strLeg.side;
	        }
            */
			legDetails[i].originalQuantity = strategyOrder.originalQuantity * strLeg.ratioQuantity;
			legDetails[i].leavesQuantity = legDetails[i].originalQuantity;
			legDetails[i].tradedQuantity = 0;
			legDetails[i].cancelledQuantity = 0;
	    }
	    return legDetails;
	}

	/*
	 * Create LegOrderDetailStruct for the given strategy order.
	 */
	public static LegOrderDetailStruct[]  createLegOrderDetails(OrderStruct strategyOrder)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
	{
		StrategyStruct product = getStrategyByProductKey(strategyOrder.productKey);
		LegOrderEntryStruct[] legEntryDetails = createDefaultLegs(strategyOrder, product);
		return createLegOrderDetails(strategyOrder, legEntryDetails, product.strategyLegs);
	}

	/*
	 * Create LegOrderDetailStruct when a strategy order is supplied with its LegOrderEntryStruct's.
	 */
	public static LegOrderDetailStruct[]  createLegOrderDetails(OrderStruct strategyOrder, LegOrderEntryStruct[] legEntryDetails)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
	{
		StrategyStruct product = getStrategyByProductKey(strategyOrder.productKey);
		return createLegOrderDetails(strategyOrder, legEntryDetails, product.strategyLegs);
	}

    /*
	 * Create LegOrderDetailStruct when a strategy order is supplied with its LegOrderEntryStructV2's.
	 */
	public static LegOrderDetailStruct[]  createLegOrderDetails(OrderStruct strategyOrder, LegOrderEntryStructV2[] legEntryDetailsV2)
		throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
	{
		StrategyStruct product = getStrategyByProductKey(strategyOrder.productKey);
		return createLegOrderDetails(strategyOrder, legEntryDetailsV2, product.strategyLegs);
	}


public static void publishNewMessage(BObject source, ChannelAdapter channelAdapter, OrderStruct newOrder)
    {
		GroupOrderStructContainer newOrderContainer = createNewMessage(newOrder);

		StringBuilder sb = new StringBuilder(110);
		sb.append("internal event received -> NewOrder : Seq # ");
		sb.append(newOrderContainer.getOrderStruct().transactionSequenceNumber);
		sb.append(getOrderIdString(newOrderContainer.getOrderStruct().orderId));
		sb.append(": statusChange=");
		sb.append(newOrderContainer.getStatusChange());
		Log.information(source,sb.toString());

        ChannelKey channelKey = new ChannelKey(ChannelKey.NEW_ORDER, newOrderContainer.getOrderStruct().userId);
        ChannelEvent event = channelAdapter.getChannelEvent(source, channelKey, newOrderContainer);
//        channelAdapter.dispatch(event);
        OrderQueryCacheFactory.find(newOrderContainer.getOrderStruct().userId).enqueue(event);

        ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(
        					newOrderContainer.getOrderStruct().orderId.executingOrGiveUpFirm);
        channelKey = new ChannelKey(ChannelKey.NEW_ORDER_BY_FIRM, firmKeyContainer);
        event = channelAdapter.getChannelEvent(source, channelKey, newOrderContainer);
        channelAdapter.dispatch(event);
    }

	
  

    public static String getOrderIdString(OrderIdStruct orderId)
    {
        StringBuilder toStr = new StringBuilder(48);
//Printed in this format -> CBOE:690:PPO:12
        toStr.append(" Order:");
        toStr.append(orderId.executingOrGiveUpFirm.exchange).append(':');
        toStr.append(orderId.executingOrGiveUpFirm.firmNumber).append(':');
        toStr.append(orderId.branch).append(':').append(orderId.branchSequenceNumber);
        toStr.append(':').append(orderId.orderDate);
        if (orderId.highCboeId > 0 || orderId.lowCboeId > 0)
        {
        	toStr.append(':').append(orderId.highCboeId).append(':').append(orderId.lowCboeId);
        }
        return toStr.toString();
    }

    public static boolean generateNewMsg()
    {
    	return generateNewMsg;
    }
    
    public static boolean disableNewAck()
    {
    	return disableNewAck;
    }

    public static long getFinishedOrderMinLifeTime()
    {
        return finishedOrderMinLifeTime;
    }

    public static boolean checkFinishedMap()
    {
        return checkFinishedMap;
    }

    public static boolean checkNewQueue()
    {
        return checkNewQueue;
    }

    public static boolean useNoWorkingOrder()
    {
        return useNoWorkingOrder;
    }

    public static boolean isExpressOrder(OrderStruct order)
    {
        if (order.contingency.type == ContingencyTypes.IOC)
        {
            for (String s : expoSessions)
            {
                if (s.equals(order.activeSession))
                {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean ackOrder(OrderStruct order, IOrderAckConstraints orderAckConstraints)
    {
        boolean ack = true;

        if (isExpressOrder(order)  || order.source == Sources.LIGHT  )
        {
            ack = false;
        }
        else if (order.orderOriginType == com.cboe.idl.cmiConstants.OrderOrigins.MARKET_MAKER_IN_CROWD ||
       		 order.orderOriginType == com.cboe.idl.cmiConstants.OrderOrigins.CTI3Origin1 ||
       		 order.orderOriginType == com.cboe.idl.cmiConstants.OrderOrigins.FIRM_FBW_ICM ||
       		 order.orderOriginType == com.cboe.idl.cmiConstants.OrderOrigins.BROKER_DEALER_FBW_ICM)
       {
        	// I,G,J, and K type orders are not acked
            if (orderAckConstraints.isSuppressSession(order.activeSession))
            {
                ack = false;
            }
        }
        return ack;
    }
    // TODO
    // Current plan is not to do TLTC at all; So, it returns false always
    public static boolean generateTLTC()
    {
    	return false;
    }



/*  TODO - if we ever decide to do TLTC, complete the following code.
    public static GroupCancelReportContainer createCancelReportTLTC(CancelRequestStruct cancelRequest, OrderStruct orderCancelReportTLTC(CancelRequestStruct cancelRequest, OrderStruct order)
    {
    	
    }
    
    public static void publishTLTC(BObject source, ChannelAdapter channelAdapter, CancelRequestStruct cancelRequest, OrderStruct order)
    {
    	GroupCancelReportContainer cancelReportContainer = createCancelReportTLTC(cancelRequest,order);
		
		StringBuilder sb = new StringBuilder(150);
		sb.append("internal event received -> CancelReport TLTC : Seq # ");
		sb.append(cancelReportContainer.getOrderStruct().transactionSequenceNumber);
		sb.append(getOrderIdString(cancelReportContainer.getOrderStruct().orderId));
		sb.append(": statusChange=");
		sb.append(cancelReportContainer.getStatusChange());
		Log.information(source,sb.toString());
		
        ChannelKey channelKey = new ChannelKey(ChannelKey.CANCEL_REPORT, cancelReportContainer.getOrderStruct().userId);
        ChannelEvent event = channelAdapter.getChannelEvent(source, channelKey, cancelReportContainer);
//        channelAdapter.dispatch(event);
        OrderQueryCacheFactory.find(cancelReportContainer.getOrderStruct().userId).enqueue(event);
    	
    }
*/
}
