package com.cboe.consumers.eventChannel;

import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumer;

public class RemoteCASExpectedOpeningPriceEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASExpectedOpeningPriceEventConsumer implements RemoteCASExpectedOpeningPriceConsumer
{

    private RemoteCASExpectedOpeningPriceConsumer delegate;

    public RemoteCASExpectedOpeningPriceEventConsumerImpl(RemoteCASExpectedOpeningPriceConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void subscribeExpectedOpeningPriceForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeExpectedOpeningPriceForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener,
            actionOnQueue);
    }

    public void subscribeExpectedOpeningPriceForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeExpectedOpeningPriceForProductV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener,
            actionOnQueue);
    }

    public void unsubscribeExpectedOpeningPriceForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
    {
        delegate.unsubscribeExpectedOpeningPriceForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeExpectedOpeningPriceForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
    {
           delegate.unsubscribeExpectedOpeningPriceForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    public void subscribeExpectedOpeningPriceForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
    {
        delegate.subscribeExpectedOpeningPriceForClass(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }


    public void unsubscribeExpectedOpeningPriceForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
    {
        delegate.unsubscribeExpectedOpeningPriceForClass(
            routingParameters,
            casOrigin,
            userId,
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
