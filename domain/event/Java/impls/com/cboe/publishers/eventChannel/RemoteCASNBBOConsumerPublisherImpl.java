package com.cboe.publishers.eventChannel;

/**
 * RemoteCASNBBOConsumerPublisherImpl.
 *
 * @author Eric J. Fredericks
 */

import com.cboe.idl.events.RemoteCASNBBOEventConsumer;
import com.cboe.interfaces.events.RemoteCASNBBOConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.util.RoutingParameterStruct;

import com.cboe.idl.cmiCallbackV2.CMINBBOConsumer;

public class RemoteCASNBBOConsumerPublisherImpl extends BObject implements RemoteCASNBBOConsumer
{
    private RemoteCASNBBOEventConsumer delegate;


    public RemoteCASNBBOConsumerPublisherImpl(RemoteCASNBBOEventConsumer delegate)
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
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeNBBOForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeNBBOForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener,
                actionOnQueue);
        }

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
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeNBBOForProductV2" +
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
           delegate.subscribeNBBOForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener,
                actionOnQueue);
        }
    }

    public void unsubscribeNBBOForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeNBBOForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeNBBOForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void unsubscribeNBBOForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeNBBOForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeNBBOForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }

        public void subscribeNBBOForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeNBBOForClass" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeNBBOForClass(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void subscribeNBBOForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeNBBOForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.subscribeNBBOForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }
    }

    public void unsubscribeNBBOForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeNBBOForClass" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeNBBOForClass(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void unsubscribeNBBOForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeNBBOForProduct" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeNBBOForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }

}
