package com.cboe.publishers.eventChannel;

/**
 * RemoteCASExpectedOpeningPriceConsumerPublisherImpl.
 *
 * @author Eric J. Fredericks
 */

import com.cboe.idl.events.RemoteCASExpectedOpeningPriceEventConsumer;
import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.util.RoutingParameterStruct;

import com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer;

public class RemoteCASExpectedOpeningPriceConsumerPublisherImpl extends BObject implements RemoteCASExpectedOpeningPriceConsumer
{
    private RemoteCASExpectedOpeningPriceEventConsumer delegate;


    public RemoteCASExpectedOpeningPriceConsumerPublisherImpl(RemoteCASExpectedOpeningPriceEventConsumer delegate)
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
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeExpectedOpeningPriceForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeExpectedOpeningPriceForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener,
                actionOnQueue);
        }

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
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeExpectedOpeningPriceForProductV2" +
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
           delegate.subscribeExpectedOpeningPriceForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener,
                actionOnQueue);
        }
    }

    public void unsubscribeExpectedOpeningPriceForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeExpectedOpeningPriceForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeExpectedOpeningPriceForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void unsubscribeExpectedOpeningPriceForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeExpectedOpeningPriceForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeExpectedOpeningPriceForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }
    public void subscribeExpectedOpeningPriceForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeExpectedOpeningPriceForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.subscribeExpectedOpeningPriceForClass(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void unsubscribeExpectedOpeningPriceForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeExpectedOpeningPriceForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeExpectedOpeningPriceForClass(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

}
