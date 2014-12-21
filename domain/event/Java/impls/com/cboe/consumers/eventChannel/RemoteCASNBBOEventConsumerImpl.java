package com.cboe.consumers.eventChannel;

import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.RemoteCASNBBOConsumer;


public class RemoteCASNBBOEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASNBBOEventConsumer implements RemoteCASNBBOConsumer
{

    private RemoteCASNBBOConsumer delegate;

    public RemoteCASNBBOEventConsumerImpl(RemoteCASNBBOConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void subscribeNBBOForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeNBBOForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener,
            actionOnQueue);
    }

    public void subscribeNBBOForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeNBBOForProductV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener,
            actionOnQueue);
    }

    public void unsubscribeNBBOForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
    {
        delegate.unsubscribeNBBOForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeNBBOForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
    {
           delegate.unsubscribeNBBOForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    public void subscribeNBBOForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        delegate.subscribeNBBOForClass(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void subscribeNBBOForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        delegate.subscribeNBBOForProduct(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener);
    }

    public void unsubscribeNBBOForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        delegate.unsubscribeNBBOForClass(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeNBBOForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
           delegate.unsubscribeNBBOForProduct(
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
