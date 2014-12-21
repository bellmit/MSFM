package com.cboe.consumers.eventChannel;

import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.RemoteCASCurrentMarketConsumer;

public class RemoteCASCurrentMarketEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASCurrentMarketEventConsumer implements RemoteCASCurrentMarketConsumer
{

    private RemoteCASCurrentMarketConsumer delegate;

    public RemoteCASCurrentMarketEventConsumerImpl(RemoteCASCurrentMarketConsumer delegate)
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
		delegate.subscribeCurrentMarketForClassV3(
		routingParameters,
		casOrigin,
		userId,
		userSessionIOR,
		clientListener,
		actionOnQueue);
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
		delegate.subscribeCurrentMarketForProductV3(
			routingParameters,
			casOrigin,
			userId,
			userSessionIOR,
			productKey,
			clientListener,
			actionOnQueue);

	}

	public void unsubscribeCurrentMarketForClassV3(
			RoutingParameterStruct routingParameters,
			String casOrigin,
			String userId,
			String userSessionIOR,
			com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
	{
		delegate.unsubscribeCurrentMarketForClassV3(
			routingParameters,
			casOrigin,
			userId,
			userSessionIOR,
			clientListener);

	}

	public void unsubscribeCurrentMarketForProductV3(
			RoutingParameterStruct routingParameters,
			String casOrigin,
			String userId,
			String userSessionIOR,
			int productKey,
			com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
	{
		delegate.unsubscribeCurrentMarketForProductV3(
			 routingParameters,
			 casOrigin,
			 userId,
			 userSessionIOR,
			 productKey,
			 clientListener);

	}

    public void subscribeCurrentMarketForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeCurrentMarketForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener,
            actionOnQueue);
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
        delegate.subscribeCurrentMarketForProductV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener,
            actionOnQueue);
    }

    public void unsubscribeCurrentMarketForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
    {
        delegate.unsubscribeCurrentMarketForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeCurrentMarketForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
    {
           delegate.unsubscribeCurrentMarketForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    public void subscribeCurrentMarketForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        delegate.subscribeCurrentMarketForClass(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void subscribeCurrentMarketForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        delegate.subscribeCurrentMarketForProduct(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener);
    }

    public void unsubscribeCurrentMarketForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        delegate.unsubscribeCurrentMarketForClass(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeCurrentMarketForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
           delegate.unsubscribeCurrentMarketForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
        throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
