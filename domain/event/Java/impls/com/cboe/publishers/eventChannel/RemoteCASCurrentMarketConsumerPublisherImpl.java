package com.cboe.publishers.eventChannel;

/**
 * RemoteCASCurrentMarketConsumerPublisherImpl.
 *
 * @author Eric J. Fredericks
 */

import com.cboe.idl.events.RemoteCASCurrentMarketEventConsumer;
import com.cboe.interfaces.events.RemoteCASCurrentMarketConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.util.RoutingParameterStruct;

public class RemoteCASCurrentMarketConsumerPublisherImpl extends BObject implements RemoteCASCurrentMarketConsumer
{
    private RemoteCASCurrentMarketEventConsumer delegate;


    public RemoteCASCurrentMarketConsumerPublisherImpl(RemoteCASCurrentMarketEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

  	public void subscribeCurrentMarketForClassV3(
		  RoutingParameterStruct routingParameters,
		  String casOrigin,
		  String userId,
		  String userSessionIOR,
		  com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener,
		  short actionOnQueue)
  	{
	  if(Log.isDebugOn())
	  {
		  Log.debug(this, "Publishing subscribeCurrentMarketForClassV3" +
				  "; casOrigin= " + casOrigin +
				  "; userId=" + userId +
				  "; session=" + routingParameters.sessionName +
				  "; classKey=" + routingParameters.classKey +
				  "; productType=" + routingParameters.productType +
				  "; actionOnQueue=" + actionOnQueue + ".");
	  }

	  if (delegate != null)
	  {
		 delegate.subscribeCurrentMarketForClassV3(
			  routingParameters,
			  casOrigin,
			  userId,
			  userSessionIOR,
			  clientListener,
			  actionOnQueue);
	  }

  }

  public void subscribeCurrentMarketForProductV3(
		  RoutingParameterStruct routingParameters,
		  String casOrigin,
		  String userId,
		  String userSessionIOR,
		  int productKey,
		  com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener,
		  short actionOnQueue)
  {
	  if(Log.isDebugOn())
	  {
		  Log.debug(this, "Publishing subscribeCurrentMarketForProductV3" +
				  "; casOrigin= " + casOrigin +
				  "; userId=" + userId +
				  "; session=" + routingParameters.sessionName +
				  "; classKey=" + routingParameters.classKey +
				  "; productKey=" + productKey +
				  "; productType=" + routingParameters.productType +
				  "; actionOnQueue=" + actionOnQueue + ".");
	  }

	  if (delegate != null)
	  {
		 delegate.subscribeCurrentMarketForProductV3(
			  routingParameters,
			  casOrigin,
			  userId,
			  userSessionIOR,
			  productKey,
			  clientListener,
			  actionOnQueue);
	  }
  }

  public void unsubscribeCurrentMarketForClassV3(
		  RoutingParameterStruct routingParameters,
		  String casOrigin,
		  String userId,
		  String userSessionIOR,
		  com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
  {
	  if(Log.isDebugOn())
	  {
		  Log.debug(this, "Publishing unsubscribeCurrentMarketForClassV3" +
				  "; casOrigin= " + casOrigin +
				  "; userId=" + userId +
				  "; session=" + routingParameters.sessionName +
				  "; classKey=" + routingParameters.classKey +
				  "; productType=" + routingParameters.productType + ".");
	  }

	  if (delegate != null)
	  {
		 delegate.unsubscribeCurrentMarketForClassV3(
			  routingParameters,
			  casOrigin,
			  userId,
			  userSessionIOR,
			  clientListener);
	  }

  }

  public void unsubscribeCurrentMarketForProductV3(
		  RoutingParameterStruct routingParameters,
		  String casOrigin,
		  String userId,
		  String userSessionIOR,
		  int productKey,
		  com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
  {
	  if(Log.isDebugOn())
	  {
		  Log.debug(this, "Publishing unsubscribeCurrentMarketForProductV3" +
				  "; casOrigin= " + casOrigin +
				  "; userId=" + userId +
				  "; session=" + routingParameters.sessionName +
				  "; classKey=" + routingParameters.classKey +
				  "; productKey=" + productKey +
				  "; productType=" + routingParameters.productType +".");
	  }

	  if (delegate != null)
	  {
		 delegate.unsubscribeCurrentMarketForProductV3(
			  routingParameters,
			  casOrigin,
			  userId,
			  userSessionIOR,
			  productKey,
			  clientListener);
	  }

  }

//

    public void subscribeCurrentMarketForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeCurrentMarketForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeCurrentMarketForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener,
                actionOnQueue);
        }

    }

    public void subscribeCurrentMarketForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeCurrentMarketForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeCurrentMarketForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener,
                actionOnQueue);
        }
    }

    public void unsubscribeCurrentMarketForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeCurrentMarketForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeCurrentMarketForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void unsubscribeCurrentMarketForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeCurrentMarketForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeCurrentMarketForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }

    public void subscribeCurrentMarketForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeCurrentMarketForClass" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeCurrentMarketForClass(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void subscribeCurrentMarketForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeCurrentMarketForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeCurrentMarketForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }
    }

    public void unsubscribeCurrentMarketForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeCurrentMarketForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeCurrentMarketForClass(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void unsubscribeCurrentMarketForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeCurrentMarketForProduct" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeCurrentMarketForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }

}
