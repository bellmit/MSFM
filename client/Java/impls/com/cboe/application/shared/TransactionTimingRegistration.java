package com.cboe.application.shared;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class TransactionTimingRegistration 
{
	private static long orderEmitPoint =0;
    private static long lightOrderEmitPoint;
    private static long orderRoutingProxyEmitPoint;
	private static long cancelOrderEmitPoint = 1;
    private static long cancelLightOrderEmitPoint;
    private static long cancelOrderRoutingProxyEmitPoint;
	private static long cancelReplaceOrderEmitPoint = 2; 
    private static long cancelReplaceOrderRoutingProxyEmitPoint;
	private static long rfqEmitPoint = 3;
    private static long rfqRoutingProxyEmitPoint;
	private static long updateOrderEmitPoint = 4;
    private static long updateOrderRoutingProxyEmitPoint;
	private static long quoteEmitPoint =5;
    private static long quoteRoutingProxyEmitPoint;
	private static long cancelQuoteEmitPoint =6;
    private static long cancelQuoteRoutingProxyEmitPoint;
    private static long omtDirectRerouteOrderEmitPoint; 
    private static long omtManualCancelOrderEmitpoint;
    private static long omtManualCancelReplaceOrderEmitpoint;
    private static long omtDirectRerouteOrderRoutingProxyEmitPoint; 
    private static long omtManualCancelOrderRoutingProxyEmitpoint;
    private static long omtManualCancelReplaceOrderRoutingProxyEmitpoint;
    private static long manualQuoteRoutingProxyEmitPoint;
    private static long cancelManualQuoteRoutingProxyEmitPoint;
    private static long manualQuoteEmitPoint;
    private static long cancelManualQuoteEmitPoint;
    

    private static long fixOrderDispatcherEmitPoint = 11;
    private static long fixLightOrderDispatcherEmitPoint;
    private static long fixCancelDispatcherEmitPoint = 12;
    private static long fixLightOrderCancelDispatcherEmitPoint;
    private static long fixCxlReDispatcherEmitPoint = 13;
    private static long fixSpreadOrderDispatcherEmitPoint = 14;
    private static long fixOrderListDispatcherEmitPoint = 15;
    private static long fixSpreadOrderListDispatcherEmitPoint = 16;

    private static long fixOrderProcessorEmitPoint = 21;
    private static long fixCancelProcessorEmitPoint = 22;
    private static long fixCxlReProcessorEmitPoint = 23;
    private static long fixSpreadOrderProcessorEmitPoint = 24;
    private static long fixOrderListProcessorEmitPoint = 25;
    private static long fixSpreadOrderListProcessorEmitPoint = 26;

    private static long fixOrderConcurrentProcessorEmitPoint = 31;
    private static long fixCancelConcurrentProcessorEmitPoint = 32;
    private static long fixCxlReConcurrentProcessorEmitPoint = 33;
    private static long fixSpreadOrderConcurrentProcessorEmitPoint = 34;
    private static long fixOrderListConcurrentProcessorEmitPoint = 35;
    private static long fixSpreadOrderListConcurrentProcessorEmitPoint = 36;
    private static long fixAppiaAdapterImplEmitPoint= 37;
    private static long fixGetOrderByIdEmitPoint= 38;
    private static long fixGetProductEmitPoint=39;
    private static long userEnablementEmitPoint = 40;
    private static long rateMonitorEmitPoint = 41;
    private static long routingBCLookupEmitPoint = 42;
    private static long fixOrderPublishNewEmitPoint = 43;


	public static long FE_NEW_ORDER_COLLECTOR_TYPE;
    public static long FE_CANCEL_REPLACE_COLLECTOR_TYPE;
    public static long FE_STRATEGY_ORDER_COLLECTOR_TYPE;
    public static long FE_STRATEGY_CANCEL_REPLACE_COLLECTOR_TYPE;
    public static long FE_CANCEL_ORDER_COLLECTOR_TYPE;
    public static long FE_STRATEGY_CANCEL_COLLECTOR_TYPE;
    public static long FE_ORDER_UPDATE_COLLECTOR_TYPE;
    public static long FE_STRATEGY_UPDATE_COLLECTOR_TYPE;
    public static long FE_DELETE_BY_BROKER_TYPE;
    
    public static long QUOTE_COLLECTOR_TYPE= 0;
    public static long QUOTE_BLOCK_COLLECTOR_TYPE = 1;
    public static long CANCEL_QUOTE_COLLECTOR_TYPE = 2;
    public static long QUOTE_CANCEL_BY_CLASS_COLLECTOR_TYPE = 3;
    public static long RFQ_COLLECTOR_TYPE = 4;
    
    
    public static void registerFETransactionIdentifier()
    {
    	if (TransactionTimingUtil.getTT()!=null)
    	{
	    	if (TransactionTimingConfiguration.publishTT())
	    	{
	    		try
	    		{
                    QUOTE_COLLECTOR_TYPE = registerTTTransactionIdentifier("QuoteTiming");
                    QUOTE_BLOCK_COLLECTOR_TYPE = registerTTTransactionIdentifier("QuoteBlockTiming");
                    CANCEL_QUOTE_COLLECTOR_TYPE = registerTTTransactionIdentifier("CancelQuoteTiming");
                    QUOTE_CANCEL_BY_CLASS_COLLECTOR_TYPE = registerTTTransactionIdentifier("CancelQuoteByClassTiming");
                    RFQ_COLLECTOR_TYPE = registerTTTransactionIdentifier("RFQTiming");

                    FE_NEW_ORDER_COLLECTOR_TYPE = registerTTTransactionIdentifier("FrontendOrderTiming");
                    FE_CANCEL_REPLACE_COLLECTOR_TYPE = registerTTTransactionIdentifier("FrontendCancelReplaceTiming");
                    FE_STRATEGY_ORDER_COLLECTOR_TYPE = registerTTTransactionIdentifier("FrontendStrategyOrderTiming");
                    FE_STRATEGY_CANCEL_REPLACE_COLLECTOR_TYPE = registerTTTransactionIdentifier("FrontendStrategyCancelReplaceTiming");
                    FE_CANCEL_ORDER_COLLECTOR_TYPE = registerTTTransactionIdentifier("FrontendCancelTiming");
                    FE_STRATEGY_CANCEL_COLLECTOR_TYPE = registerTTTransactionIdentifier("FrontendStrategyCancelTiming");
                    FE_ORDER_UPDATE_COLLECTOR_TYPE = registerTTTransactionIdentifier("FrontendOrderUpdateTiming");
                    FE_STRATEGY_UPDATE_COLLECTOR_TYPE = registerTTTransactionIdentifier("FrontendStrategyUpdateTiming");
                    FE_DELETE_BY_BROKER_TYPE = registerTTTransactionIdentifier("FrontendDeleteByBrokerTiming");
			        Log.information("Registration of TT identifiers for Order and Quote is completed");
	
                }
                catch (Exception e)
	            {
	                Log.exception("Unable to register TT identifier for Order and Quote. Exception encountered: ",e);
	            }
	    	}
    	}
    }

	public static void registerCasTransactionIdentifier()
    {
		if (TransactionTimingUtil.getTTE()!=null)
		{
			if (TransactionTimingConfiguration.publishOrderTTE())
	        // only need to register order emit points if the TTE indicator is on
	        {
	            try
	            {
                    orderEmitPoint = registerTransactionIdentifier("Order");
                    lightOrderEmitPoint = registerTransactionIdentifier("LightOrder");
                    orderRoutingProxyEmitPoint = registerTransactionIdentifier("Order_RoutingProxy");
                    cancelOrderEmitPoint = registerTransactionIdentifier("CancelOrder");
                    cancelLightOrderEmitPoint = registerTransactionIdentifier("CancelLightOrder");
                    cancelOrderRoutingProxyEmitPoint = registerTransactionIdentifier("CancelOrder_RoutingProxy");
                    cancelReplaceOrderEmitPoint = registerTransactionIdentifier("CancelReplaceOrder");
                    cancelReplaceOrderRoutingProxyEmitPoint = registerTransactionIdentifier("CancelReplaceOrder_RoutingProxy");
                    updateOrderEmitPoint = registerTransactionIdentifier("UpdateOrder");
                    updateOrderRoutingProxyEmitPoint = registerTransactionIdentifier("UpdateOrder_RoutingProxy");
                    omtDirectRerouteOrderEmitPoint = registerTransactionIdentifier("OMTDirectRerouteOrder");
                    omtManualCancelOrderEmitpoint = registerTransactionIdentifier("OMTManaulCancelOrder");
                    omtManualCancelReplaceOrderEmitpoint = registerTransactionIdentifier("OMTManualCancelReplaceOrder");
                    omtDirectRerouteOrderRoutingProxyEmitPoint = registerTransactionIdentifier("OMTDirectRerouteOrder_RoutingProxy");
                    omtManualCancelOrderRoutingProxyEmitpoint = registerTransactionIdentifier("OMTManaulCancelOrder_RoutingProxy");
                    omtManualCancelReplaceOrderRoutingProxyEmitpoint = registerTransactionIdentifier("OMTManualCancelReplaceOrder_RoutingProxy");
                
                userEnablementEmitPoint = TransactionTimingUtil.getTTE().registerTransactionIdentifier( "UserEnablementCheck" );
                rateMonitorEmitPoint = TransactionTimingUtil.getTTE().registerTransactionIdentifier( "RateMonitorCheck" );
                routingBCLookupEmitPoint = TransactionTimingUtil.getTTE().registerTransactionIdentifier( "RoutingBCLookup" );
                fixOrderPublishNewEmitPoint = TransactionTimingUtil.getTTE().registerTransactionIdentifier( "FixOrderPublishNew" );
	                Log.information("Registration of TTE identifiers for Order is completed");
	
                }
                catch (Exception e)
	            {
	                Log.exception("Unable to register TTE identifier for Order. Exception encountered: ",e);
	            }
	        }
	
	        if (TransactionTimingConfiguration.publishQuoteTTE())
	        // only need to register quote emit points if the TTE indicator is on
	        {
	            try
	            {
                    rfqEmitPoint = registerTransactionIdentifier("RFQ");
                    rfqRoutingProxyEmitPoint = registerTransactionIdentifier("RFQ_RoutingProxy");
                    quoteEmitPoint = registerTransactionIdentifier("Quote");
                    quoteRoutingProxyEmitPoint = registerTransactionIdentifier("Quote_RoutingProxy");
                    cancelQuoteEmitPoint = registerTransactionIdentifier("CancelQuote");
                    cancelQuoteRoutingProxyEmitPoint = registerTransactionIdentifier("CancelQuote_RoutingProxy");
	                Log.information("Registration of TTE identifiers for Quote is complete");
	
                }
                catch (Exception e)
	            {
	                Log.exception("Unable to register TTE identifier for Quote. Exception encountered: ",e);
	            }
	        }
		}

    }

    public static void registerFixTransactionIdentifier()
    {
    	if (TransactionTimingUtil.getTTE()!=null)
    	{
	        if (TransactionTimingConfiguration.publishOrderTTE())
	        // only need to register order emit points if the TTE indicator is on
	        {
	            try
	            {
                    fixOrderDispatcherEmitPoint = registerTransactionIdentifier("FixOrderDispatcher");
                    fixLightOrderDispatcherEmitPoint = registerTransactionIdentifier("FixLightOrderDispatcher");
                    fixCancelDispatcherEmitPoint = registerTransactionIdentifier("FixCancelDispatcher");
                    fixLightOrderCancelDispatcherEmitPoint = registerTransactionIdentifier("FixLightOrderCancelDispatcher");
                    fixCxlReDispatcherEmitPoint = registerTransactionIdentifier("FixCxlReDispatcher");
                    fixSpreadOrderDispatcherEmitPoint = registerTransactionIdentifier("FixSpreadOrderDispatcher");
                    fixOrderListDispatcherEmitPoint = registerTransactionIdentifier("FixOrderListDispatcher");
                    fixSpreadOrderListDispatcherEmitPoint = registerTransactionIdentifier("FixSpreadOrderListDispatcher");

                    fixOrderProcessorEmitPoint = registerTransactionIdentifier("FixOrderProcessor");
                    fixCancelProcessorEmitPoint = registerTransactionIdentifier("FixCancelProcessor");
                    fixCxlReProcessorEmitPoint = registerTransactionIdentifier("FixCxlReProcessor");
                    fixSpreadOrderProcessorEmitPoint = registerTransactionIdentifier("FixSpreadOrderProcessor");
                    fixOrderListProcessorEmitPoint = registerTransactionIdentifier("FixOrderListProcessor");
                    fixSpreadOrderListProcessorEmitPoint = registerTransactionIdentifier("FixSpreadOrderListProcessor");

                    fixOrderConcurrentProcessorEmitPoint = registerTransactionIdentifier("FixOrderConcurrentProcessor");
                    fixCancelConcurrentProcessorEmitPoint = registerTransactionIdentifier("FixCancelConcurrentProcessor");
                    fixCxlReConcurrentProcessorEmitPoint = registerTransactionIdentifier("FixCxlReConcurrentProcessor");
                    fixSpreadOrderConcurrentProcessorEmitPoint = registerTransactionIdentifier("FixSpreadOrderConcurrentProcessor");
                    fixOrderListConcurrentProcessorEmitPoint = registerTransactionIdentifier("FixOrderListConcurrentProcessor");
                    fixSpreadOrderListConcurrentProcessorEmitPoint = registerTransactionIdentifier("FixSpreadOrderListConcurrentProcessor");
                    fixAppiaAdapterImplEmitPoint = registerTransactionIdentifier("FixAppiaAdapter");
                    fixGetOrderByIdEmitPoint = registerTransactionIdentifier("FixgetOrderByID");
                    fixGetProductEmitPoint = registerTransactionIdentifier("FixGetProductByName");

	                Log.information("Registration of Fix TTE identifiers for Order is completed");
                }
                catch (Exception e)
	            {
	                Log.exception("Unable to register Fix TTE identifier for Order. Exception encountered: ",e);
	            }
	        }
	
	        if (TransactionTimingConfiguration.publishQuoteTTE())
	        // only need to register quote emit points if the TTE indicator is on
	        {
	            try
	            {
                    rfqEmitPoint = registerTransactionIdentifier("RFQ");
                    rfqRoutingProxyEmitPoint = registerTransactionIdentifier("RFQ_RoutingProxy");
                    quoteEmitPoint = registerTransactionIdentifier("Quote");
                    quoteRoutingProxyEmitPoint = registerTransactionIdentifier("Quote_RoutingProxy");
                    cancelQuoteEmitPoint = registerTransactionIdentifier("CancelQuote");
                    cancelQuoteRoutingProxyEmitPoint = registerTransactionIdentifier("CancelQuote_RoutingProxy");
                    manualQuoteRoutingProxyEmitPoint = registerTransactionIdentifier("ManualQuote_RoutingProxy");
                    cancelManualQuoteRoutingProxyEmitPoint = registerTransactionIdentifier("CancelManualQuote_RoutingProxy");
                    manualQuoteEmitPoint = registerTransactionIdentifier("ManualQuote");
                    cancelManualQuoteEmitPoint = registerTransactionIdentifier("CancelManualQuote");
	                Log.information("Registration of Fix TTE identifiers for Quote is complete");
	
                }
                catch (Exception e)
	            {
	                Log.exception("Unable to register Fix TTE identifier for Quote. Exception encountered: ",e);
	            }
	        }
    	}

    }

    public static long getManualQuoteRoutingProxyEmitPoint()
    {
    	return manualQuoteRoutingProxyEmitPoint;
    }

    public static long getCancelManualQuoteRoutingProxyEmitPoint()
    {
    	return cancelManualQuoteRoutingProxyEmitPoint;
    }

    public static long getManualQuoteEmitPoint()
    {
    	return manualQuoteEmitPoint;
    }

    public static long getCancelManualQuoteEmitPoint()
    {
    	return cancelManualQuoteEmitPoint;
    }

    public static long getOmtDirectRerouteOrderRoutingProxyEmitPoint()
	{
		return omtDirectRerouteOrderRoutingProxyEmitPoint;
	}

    public static long getOmtManaulCancelOrderRoutingProxyEmitPoint()
	{
		return omtManualCancelOrderRoutingProxyEmitpoint;
	}

    public static long getOmtManaulCancelReplaceOrderRoutingProxyEmitPoint()
	{
		return omtManualCancelReplaceOrderRoutingProxyEmitpoint;
	}

    public static long getOmtDirectRerouteOrderEmitPoint()
	{
		return omtDirectRerouteOrderEmitPoint;
	}

    public static long getOmtManaulCancelOrderEmitPoint()
	{
		return omtManualCancelOrderEmitpoint;
	}

    public static long getOmtManaulCancelReplaceOrderEmitPoint()
	{
		return omtManualCancelReplaceOrderEmitpoint;
	}

	public static long getOrderEmitPoint()
	{
		return orderEmitPoint;
	}

    public static long getLightOrderEmitPoint()
    {
        return lightOrderEmitPoint;
    }

    public static long getOrderRoutingProxyEmitPoint()
    {
        return orderRoutingProxyEmitPoint;
    }
    
	public static long getCancelOrderEmitPoint()
	{
		return cancelOrderEmitPoint;
	}

    public static long getCancelLightOrderEmitPoint()
    {
        return cancelLightOrderEmitPoint;
    }

    public static long getCancelOrderRoutingProxyEmitPoint()
	{
		return cancelOrderRoutingProxyEmitPoint;
	}

	public static long getCancelReplaceOrderEmitPoint()
	{
		return cancelReplaceOrderEmitPoint;
	}

    public static long getCancelReplaceOrderRoutingProxyEmitPoint()
    {
        return cancelReplaceOrderRoutingProxyEmitPoint;
    }

    public static long getFixOrderDispatcherEmitPoint()
	{
		return fixOrderDispatcherEmitPoint;
	}

    public static long getFixLightOrderDispatcherEmitPoint()
    {
        return fixLightOrderDispatcherEmitPoint;
    }

    public static long getFixCancelDispatcherEmitPoint()
	{
		return fixCancelDispatcherEmitPoint;
	}

    public static long getFixLightOrderCancelDispatcherEmitPoint()
    {
        return fixLightOrderCancelDispatcherEmitPoint;
    }

    public static long getFixCxlReDispatcherEmitPoint()
	{
		return fixCxlReDispatcherEmitPoint;
	}

    public static long getFixSpreadOrderDispatcherEmitPoint()
	{
		return fixSpreadOrderDispatcherEmitPoint;
	}

    public static long getFixOrderListDispatcherEmitPoint()
	{
		return fixOrderListDispatcherEmitPoint;
	}

    public static long getFixSpreadOrderListDispatcherEmitPoint()
	{
		return fixSpreadOrderListDispatcherEmitPoint;
	}

    public static long getFixOrderProcessorEmitPoint()
    {
        return fixOrderProcessorEmitPoint;
    }

    public static long getFixCancelProcessorEmitPoint()
    {
        return fixCancelProcessorEmitPoint;
    }

    public static long getFixCxlReProcessorEmitPoint()
    {
        return fixCxlReProcessorEmitPoint;
    }

    public static long getFixSpreadOrderProcessorEmitPoint()
    {
        return fixSpreadOrderProcessorEmitPoint;
    }

    public static long getFixOrderListProcessorEmitPoint()
    {
        return fixOrderListProcessorEmitPoint;
    }

    public static long getFixSpreadOrderListProcessorEmitPoint()
    {
        return fixSpreadOrderListProcessorEmitPoint;
    }

    public static long getFixOrderConcurrentProcessorEmitPoint()
    {
        return fixOrderConcurrentProcessorEmitPoint;
    }

    public static long getFixCancelConcurrentProcessorEmitPoint()
    {
        return fixCancelConcurrentProcessorEmitPoint;
    }

    public static long getFixCxlReConcurrentProcessorEmitPoint()
    {
        return fixCxlReConcurrentProcessorEmitPoint;
    }

    public static long getFixSpreadOrderConcurrentProcessorEmitPoint()
    {
        return fixSpreadOrderConcurrentProcessorEmitPoint;
    }

    public static long getFixOrderListConcurrentProcessorEmitPoint()
    {
        return fixOrderListConcurrentProcessorEmitPoint;
    }

    public static long getFixSpreadOrderListConcurrentProcessorEmitPoint()
    {
        return fixSpreadOrderListConcurrentProcessorEmitPoint;
    }

	public static long getRFQEmitPoint()
	{
		return rfqEmitPoint;
	}

    public static long getRFQRoutingProxyEmitPoint()
    {
        return rfqRoutingProxyEmitPoint;
    }

	public static long getUpdateOrderEmitPoint()
	{
		return updateOrderEmitPoint;
	}

    public static long getUpdateOrderRoutingProxyEmitPoint()
    {
        return updateOrderRoutingProxyEmitPoint;
    }

	public static long getQuoteEmitPoint()
	{
		return quoteEmitPoint;
	}

    public static long getQuoteRoutingProxyEmitPoint()
    {
        return quoteRoutingProxyEmitPoint;
    }

	public static long getCancelQuoteEmitPoint()
	{
		return cancelQuoteEmitPoint;
	}

    public static long getCancelQuoteRoutingProxyEmitPoint()
    {
        return cancelQuoteRoutingProxyEmitPoint;
	}

    public static long getFixAppiaAdapterImplEmitPoint()
    {
        return fixAppiaAdapterImplEmitPoint;
    }

    public static long getFixGetOrderByIdEmitPoint()
    {
        return fixGetOrderByIdEmitPoint;
    }

    public static long getFixGetProductEmitPoint()
    {
        return fixGetProductEmitPoint;
    }

    private static long registerTransactionIdentifier(String identifier)
    {
        long methodID = TransactionTimingUtil.getTTE().registerTransactionIdentifier(identifier);
        Log.information(new StringBuilder(100).append("TTE Method:").append(identifier).append("\t\tID:").append(methodID).toString());
        return methodID;
    }

    private static long registerTTTransactionIdentifier(String identifier)
    {
          long methodID = TransactionTimingUtil.getTT().registerTransactionIdentifier(identifier);
        Log.information(new StringBuilder(100).append("TT Method:").append(identifier).append("\t\tID:").append(methodID).toString());
        return methodID;
    }
	public static long getUserEnablementEmitPoint() {
		return userEnablementEmitPoint;
	}
	public static long getRateMonitorEmitPoint() {
		return rateMonitorEmitPoint;
	}
	public static long getRoutingBCLookupEmitPoint() {
		return routingBCLookupEmitPoint;
	}
	public static long getFixOrderPublishNewEmitPoint() {
		return fixOrderPublishNewEmitPoint;
    }

}