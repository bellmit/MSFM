package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASBookDepthConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;


public class RemoteCASBookDepthConsumerIECImpl extends BObject implements RemoteCASBookDepthConsumer
{
    private EventChannelAdapter internalEventChannel;

    public RemoteCASBookDepthConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

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
            Log.debug(this, "Consume/IEC Publish subscribeBookDepthForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, 0/*productKey - not applicable*/,
                     casOrigin, userId, userSessionIOR, clientListener, actionOnQueue);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

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
            Log.debug(this, "Consume/IEC Publish subscribeBookDepthForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, productKey,
                     casOrigin, userId, userSessionIOR, clientListener, actionOnQueue);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

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

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, 0/*productKey - not applicable*/,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0 /* action on queue - not applicable */);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeBookDepthForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, productKey,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0 /* action on queue - not applicable */);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
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
            Log.debug(this, "Consume/IEC Publish subscribeBookDepthForProduct" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, productKey,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeBookDepthForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthForProduct" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, productKey,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0 /* action on queue - not applicable */);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }
}
