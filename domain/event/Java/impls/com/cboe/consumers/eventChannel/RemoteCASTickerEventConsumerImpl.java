package com.cboe.consumers.eventChannel;

import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.RemoteCASTickerConsumer;

public class RemoteCASTickerEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASTickerEventConsumer implements RemoteCASTickerConsumer
{

    private RemoteCASTickerConsumer delegate;

    public RemoteCASTickerEventConsumerImpl(RemoteCASTickerConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void subscribeTickerForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeTickerForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener,
            actionOnQueue);
    }

    public void subscribeTickerForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeTickerForProductV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener,
            actionOnQueue);
    }

    public void unsubscribeTickerForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
    {
        delegate.unsubscribeTickerForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeTickerForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
    {
           delegate.unsubscribeTickerForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    public void subscribeTickerForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
    {
        delegate.subscribeTickerForProduct(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener);
    }

    public void unsubscribeTickerForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
    {
           delegate.unsubscribeTickerForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    // Added for VTATS
    public void subscribeLargeTradeLastSaleForClass(RoutingParameterStruct routingParameters, String casOrigin, String userId , String userSessionIOR, TickerConsumer clientListener, short actionOnQueue) {
		delegate.subscribeLargeTradeLastSaleForClass(
				routingParameters, 
				casOrigin, 
				userId , 
				userSessionIOR, 
				clientListener, 
				actionOnQueue);
	}

    //  Added for VTATS
	public void unsubscribeLargeTradeLastSaleForClass(RoutingParameterStruct routingParameters, String casOrigin, String userId , String userSessionIOR, TickerConsumer clientListener) {
		delegate.unsubscribeLargeTradeLastSaleForClass(
				routingParameters, 
				casOrigin, 
				userId , 
				userSessionIOR, 
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
