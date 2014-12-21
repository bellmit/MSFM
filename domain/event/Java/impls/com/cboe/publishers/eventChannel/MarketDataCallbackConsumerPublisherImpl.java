package com.cboe.publishers.eventChannel;

import com.cboe.idl.events.MarketDataCallbackEventConsumer;
import com.cboe.interfaces.events.MarketDataCallbackConsumer;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;
import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer;

public class MarketDataCallbackConsumerPublisherImpl extends BObject implements MarketDataCallbackConsumer
{
    private MarketDataCallbackEventConsumer delegate;

    public MarketDataCallbackConsumerPublisherImpl(MarketDataCallbackEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void subscribeCurrentMarket(RoutingParameterStruct routingParameters,
                                       String casOrigin,
                                       String userId,
                                       String userSessionIOR,
                                       com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer clientListener,
                                       short queuePolicy,
                                       boolean disseminateExternalMarketData)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing V4 subscribeCurrentMarket" +
                            "; casOrigin= " + casOrigin +
                            "; userId=" + userId +
                            "; session=" + routingParameters.sessionName +
                            "; classKey=" + routingParameters.classKey +
                            "; productType=" + routingParameters.productType +
                            "; queuePolicy=" + queuePolicy + 
                            "; disseminateExternalMarketData=" + disseminateExternalMarketData + ".");
        }

        if(delegate != null)
        {
            delegate.subscribeCurrentMarket(routingParameters,
                                            casOrigin,
                                            userId,
                                            userSessionIOR,
                                            clientListener,
                                            queuePolicy,
                                            disseminateExternalMarketData);
        }
    }

    public void unsubscribeCurrentMarket(RoutingParameterStruct routingParameters,
                                         String casOrigin,
                                         String userId,
                                         String userSessionIOR,
                                         com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing V4 unsubscribeCurrentMarket" +
                            "; casOrigin= " + casOrigin +
                            "; userId=" + userId +
                            "; session=" + routingParameters.sessionName +
                            "; classKey=" + routingParameters.classKey +
                            "; productType=" + routingParameters.productType + ".");
        }

        if(delegate != null)
        {
            delegate.unsubscribeCurrentMarket(routingParameters,
                                              casOrigin,
                                              userId,
                                              userSessionIOR,
                                              clientListener);
        }
    }

    public void subscribeTicker(RoutingParameterStruct routingParameters,
                                String casOrigin,
                                String userId,
                                String userSessionIOR,
                                CMITickerConsumer clientListener,
                                short queuePolicy,
                                boolean disseminateExternalMarketData)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing V4 subscribeTicker" +
                            "; casOrigin= " + casOrigin +
                            "; userId=" + userId +
                            "; session=" + routingParameters.sessionName +
                            "; classKey=" + routingParameters.classKey +
                            "; productType=" + routingParameters.productType +
                            "; queuePolicy=" + queuePolicy + "." +
                            "; disseminateExternalMarketData=" + disseminateExternalMarketData + ".");
        }

        if(delegate != null)
        {
            delegate.subscribeTicker(routingParameters,
                                     casOrigin,
                                     userId,
                                     userSessionIOR,
                                     clientListener,
                                     queuePolicy,
                                     disseminateExternalMarketData);
        }
    }

    public void unsubscribeTicker(RoutingParameterStruct routingParameters,
                                  String casOrigin,
                                  String userId,
                                  String userSessionIOR,
                                  CMITickerConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing V4 unsubscribeTicker" +
                            "; casOrigin= " + casOrigin +
                            "; userId=" + userId +
                            "; session=" + routingParameters.sessionName +
                            "; classKey=" + routingParameters.classKey +
                            "; productType=" + routingParameters.productType + ".");
        }

        if(delegate != null)
        {
            delegate.unsubscribeTicker(routingParameters,
                                       casOrigin,
                                       userId,
                                       userSessionIOR,
                                       clientListener);
        }
    }

    public void subscribeRecap(RoutingParameterStruct routingParameters,
                               String casOrigin,
                               String userId,
                               String userSessionIOR,
                               CMIRecapConsumer clientListener,
                               short queuePolicy,
                               boolean disseminateExternalMarketData)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing V4 subscribeRecap" +
                            "; casOrigin= " + casOrigin +
                            "; userId=" + userId +
                            "; session=" + routingParameters.sessionName +
                            "; classKey=" + routingParameters.classKey +
                            "; productType=" + routingParameters.productType +
                            "; queuePolicy=" + queuePolicy + "." +
                            "; disseminateExternalMarketData=" + disseminateExternalMarketData + ".");
        
        }

        if(delegate != null)
        {
            delegate.subscribeRecap(routingParameters,
                                    casOrigin,
                                    userId,
                                    userSessionIOR,
                                    clientListener,
                                    queuePolicy,
                                    disseminateExternalMarketData);
        }
    }

    public void unsubscribeRecap(RoutingParameterStruct routingParameters,
                                 String casOrigin,
                                 String userId,
                                 String userSessionIOR,
                                 CMIRecapConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing V4 unsubscribeRecap" +
                            "; casOrigin= " + casOrigin +
                            "; userId=" + userId +
                            "; session=" + routingParameters.sessionName +
                            "; classKey=" + routingParameters.classKey +
                            "; productType=" + routingParameters.productType + ".");
        }

        if(delegate != null)
        {
            delegate.unsubscribeRecap(routingParameters,
                                      casOrigin,
                                      userId,
                                      userSessionIOR,
                                      clientListener);
        }
    }
    
    public void subscribeNBBO(RoutingParameterStruct routingParameters,
                              String casOrigin,
                              String userId,
                              String userSessionIOR,
                              CMINBBOConsumer clientListener,
                              short queuePolicy)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing subscribeNBBO" +
			         "; casOrigin= " + casOrigin +
			         "; userId=" + userId +
			         "; session=" + routingParameters.sessionName +
			         "; classKey=" + routingParameters.classKey +
			         "; productType=" + routingParameters.productType +
			         "; queuePolicy=" + queuePolicy + ".");
		}
	
		if(delegate != null)
		{
			delegate.subscribeNBBO(routingParameters,
			                 casOrigin,
			                 userId,
			                 userSessionIOR,
			                 clientListener,
			                 queuePolicy);
		}
	}
	
	public void unsubscribeNBBO(RoutingParameterStruct routingParameters,
	                            String casOrigin,
	                            String userId,
	                            String userSessionIOR,
	                            CMINBBOConsumer clientListener)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing unsubscribeNBBO" +
			         "; casOrigin= " + casOrigin +
			         "; userId=" + userId +
			         "; session=" + routingParameters.sessionName +
			         "; classKey=" + routingParameters.classKey +
			         "; productType=" + routingParameters.productType + ".");
		}
		
		if(delegate != null)
		{
			delegate.unsubscribeNBBO(routingParameters,
			                   casOrigin,
			                   userId,
			                   userSessionIOR,
			                   clientListener);
		}
	}
	
	public void subscribeCurrentMarketForProduct(RoutingParameterStruct routingParameters,
									             String casOrigin,
									             String userId,
									             String userSessionIOR,
									             int productKey,
									             CurrentMarketManualQuoteConsumer clientListener,
									             short queuePolicy,
									             boolean disseminateExternalMarketData)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing V4 subscribeCurrentMarketForProduct" +
			 "; casOrigin= " + casOrigin +
			 "; userId=" + userId +
			 "; session=" + routingParameters.sessionName +
			 "; classKey=" + routingParameters.classKey +
			 "; productKey=" + productKey +
			 "; productType=" + routingParameters.productType +
			 "; queuePolicy=" + queuePolicy + 
			 "; disseminateExternalMarketData=" + disseminateExternalMarketData + ".");
		}
		
		if(delegate != null)
		{
			delegate.subscribeCurrentMarketForProduct(routingParameters,
			                 casOrigin,
			                 userId,
			                 userSessionIOR,
			                 productKey,
			                 clientListener,
			                 queuePolicy,
			                 disseminateExternalMarketData);
		}
	}
	
	public void unsubscribeCurrentMarketForProduct(RoutingParameterStruct routingParameters,
	              								   String casOrigin,
										           String userId,
										           String userSessionIOR,
										           int productKey,
										           CurrentMarketManualQuoteConsumer clientListener)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing V4 unsubscribeCurrentMarketForProduct" +
			 "; casOrigin= " + casOrigin +
			 "; userId=" + userId +
			 "; session=" + routingParameters.sessionName +
			 "; classKey=" + routingParameters.classKey +
			 "; productKey=" + productKey +
			 "; productType=" + routingParameters.productType + ".");
		}
		
		if(delegate != null)
		{
			delegate.unsubscribeCurrentMarketForProduct(routingParameters,
			                   casOrigin,
			                   userId,
			                   userSessionIOR,
			                   productKey,
			                   clientListener);
		}
	}
	
	public void subscribeTickerForProduct(RoutingParameterStruct routingParameters,
	                                      String casOrigin,
	                                      String userId,
	                                      String userSessionIOR,
	                                      int productKey,
	                                      CMITickerConsumer clientListener,
	                                      short queuePolicy,
	                                      boolean disseminateExternalMarketData)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing V4 subscribeTickerForProduct" +
			 "; casOrigin= " + casOrigin +
			 "; userId=" + userId +
			 "; session=" + routingParameters.sessionName +
			 "; classKey=" + routingParameters.classKey +
			 "; productKey=" + productKey +
			 "; productType=" + routingParameters.productType +
			 "; queuePolicy=" + queuePolicy + "." +
			 "; disseminateExternalMarketData=" + disseminateExternalMarketData + ".");
		}
		
		if(delegate != null)
		{
			delegate.subscribeTickerForProduct(routingParameters,
			          casOrigin,
			          userId,
			          userSessionIOR,
			          productKey,
			          clientListener,
			          queuePolicy,
			          disseminateExternalMarketData);
		}
	}
	
	public void unsubscribeTickerForProduct(RoutingParameterStruct routingParameters,
	                                        String casOrigin,
	                                        String userId,
	                                        String userSessionIOR,
	                                        int productKey,
	                                        CMITickerConsumer clientListener)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing V4 unsubscribeTickerForProduct" +
			 "; casOrigin= " + casOrigin +
			 "; userId=" + userId +
			 "; session=" + routingParameters.sessionName +
			 "; classKey=" + routingParameters.classKey +
			 "; productKey=" + productKey +
			 "; productType=" + routingParameters.productType + ".");
		}
		
		if(delegate != null)
		{
			delegate.unsubscribeTickerForProduct(routingParameters,
			            casOrigin,
			            userId,
			            userSessionIOR,
			            productKey,
			            clientListener);
		}
	}
	
	public void subscribeRecapForProduct(RoutingParameterStruct routingParameters,
	                                     String casOrigin,
	                                     String userId,
	                                     String userSessionIOR,
	                                     int productKey,
	                                     CMIRecapConsumer clientListener,
	                                     short queuePolicy,
	                                     boolean disseminateExternalMarketData)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing V4 subscribeRecapForProduct" +
			 "; casOrigin= " + casOrigin +
			 "; userId=" + userId +
			 "; session=" + routingParameters.sessionName +
			 "; classKey=" + routingParameters.classKey +
			 "; productKey=" + productKey +
			 "; productType=" + routingParameters.productType +
			 "; queuePolicy=" + queuePolicy + "." +
			 "; disseminateExternalMarketData=" + disseminateExternalMarketData + ".");
			
		}
		
		if(delegate != null)
		{
			delegate.subscribeRecapForProduct(routingParameters,
			         casOrigin,
			         userId,
			         userSessionIOR,
			         productKey,
			         clientListener,
			         queuePolicy,
			         disseminateExternalMarketData);
		}
	}
	
	public void unsubscribeRecapForProduct(RoutingParameterStruct routingParameters,
	                                       String casOrigin,
	                                       String userId,
	                                       String userSessionIOR,
	                                       int productKey,
	                                       CMIRecapConsumer clientListener)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing V4 unsubscribeRecapForProduct" +
			 "; casOrigin= " + casOrigin +
			 "; userId=" + userId +
			 "; session=" + routingParameters.sessionName +
			 "; classKey=" + routingParameters.classKey +
			 "; productKey=" + productKey +
			 "; productType=" + routingParameters.productType + ".");
		}
		
		if(delegate != null)
		{
			delegate.unsubscribeRecapForProduct(routingParameters,
			           casOrigin,
			           userId,
			           userSessionIOR,
			           productKey,
			           clientListener);
		}
	}
	
	public void subscribeNBBOForProduct(RoutingParameterStruct routingParameters,
		                                String casOrigin,
		                                String userId,
		                                String userSessionIOR,
		                                int productKey,
		                                CMINBBOConsumer clientListener,
		                                short queuePolicy)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing subscribeNBBOForProduct" +
			"; casOrigin= " + casOrigin +
			"; userId=" + userId +
			"; session=" + routingParameters.sessionName +
			"; classKey=" + routingParameters.classKey +
			"; productKey=" + productKey +
			"; productType=" + routingParameters.productType +
			"; queuePolicy=" + queuePolicy + ".");
		}
		
		if(delegate != null)
		{
			delegate.subscribeNBBOForProduct(routingParameters,
			  casOrigin,
			  userId,
			  userSessionIOR,
			  productKey,
			  clientListener,
			  queuePolicy);
		}
	}
	
	public void unsubscribeNBBOForProduct(RoutingParameterStruct routingParameters,
		                                  String casOrigin,
		                                  String userId,
		                                  String userSessionIOR,
		                                  int productKey,
		                                  CMINBBOConsumer clientListener)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing unsubscribeNBBOForProduct" +
			"; casOrigin= " + casOrigin +
			"; userId=" + userId +
			"; session=" + routingParameters.sessionName +
			"; classKey=" + routingParameters.classKey +
			"; productKey=" + productKey +
			"; productType=" + routingParameters.productType + ".");
		}
		
		if(delegate != null)
		{
			delegate.unsubscribeNBBOForProduct(routingParameters,
			    casOrigin,
			    userId,
			    userSessionIOR,
			    productKey,
			    clientListener);
		}
	}
}
