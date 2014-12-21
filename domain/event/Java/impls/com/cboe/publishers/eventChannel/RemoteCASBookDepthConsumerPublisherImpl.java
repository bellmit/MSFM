package com.cboe.publishers.eventChannel;

/**
 * RemoteCASBookDepthConsumerPublisherImpl.
 *
 * @author Eric J. Fredericks
 */
import com.cboe.idl.events.RemoteCASBookDepthEventConsumer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASBookDepthConsumer;

public class RemoteCASBookDepthConsumerPublisherImpl extends BObject implements RemoteCASBookDepthConsumer
{
    private RemoteCASBookDepthEventConsumer delegate;


    public RemoteCASBookDepthConsumerPublisherImpl(RemoteCASBookDepthEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    /**
    * This method is called by the CORBA event channel when a subscribeBookDepthForClassV2 event is
    * generated.  It adds the event to the queue and wakes the queue processing
    * thread up.
    *
    * @author Jeff Illian
    * @author Eric J. Fredericks
    */
    public void subscribeBookDepthForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeBookDepthForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeBookDepthForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener,
                actionOnQueue);
        }
   }

    /**
    * This method is called by the CORBA event channel when a subscribeBookDepthForProductV2 event is
    * generated.  It adds the event to the queue and wakes the queue processing
    * thread up.
    *
    * @author Jeff Illian
    * @author Eric J. Fredericks
    */
    public void subscribeBookDepthForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeBookDepthForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; productKey=" + productKey +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if(delegate != null)
        {
            delegate.subscribeBookDepthForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener,
                actionOnQueue);
        }

    }

    /**
    * This method is called by the CORBA event channel when a unsubscribeBookDepthForClassV2 event is
    * generated.  It adds the event to the queue and wakes the queue processing
    * thread up.
    *
    * @author Jeff Illian
    * @author Eric J. Fredericks
    */
    public void unsubscribeBookDepthForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if(delegate != null)
        {
            delegate.unsubscribeBookDepthForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }
    }

    /**
    * This method is called by the CORBA event channel when a unsubscribeBookDepthForProductV2 event is
    * generated.  It adds the event to the queue and wakes the queue processing
    * thread up.
    *
    * @author Jeff Illian
    * @author Eric J. Fredericks
    */
     public void unsubscribeBookDepthForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionManager,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeBookDepthForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; productKey=" + productKey + ".");
        }

        if(delegate != null)
        {
            delegate.unsubscribeBookDepthForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionManager,
                productKey,
                clientListener);
        }
    }
    public void subscribeBookDepthForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeBookDepthForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; productKey=" + productKey + ".");
        }

        if(delegate != null)
        {
            delegate.subscribeBookDepthForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }
    public void unsubscribeBookDepthForProduct(
           RoutingParameterStruct routingParameters,
           String casOrigin,
           String userId,
           String userSessionManager,
           int productKey,
           com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
   {
       if(Log.isDebugOn())
       {
           Log.debug(this, "Publishing subscribeBookDepthForProductV2" +
                   "; casOrigin= " + casOrigin +
                   "; userId=" + userId +
                   "; session=" + routingParameters.sessionName +
                   "; classKey=" + routingParameters.classKey +
                   "; productType=" + routingParameters.productType +
                   "; productKey=" + productKey + ".");
       }

       if(delegate != null)
       {
           delegate.unsubscribeBookDepthForProduct(
               routingParameters,
               casOrigin,
               userId,
               userSessionManager,
               productKey,
               clientListener);
       }
   }



}
